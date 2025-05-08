package com.vawndev.spring_boot_readnovel.Services.Impl;

import com.vawndev.spring_boot_readnovel.Dto.Requests.ConditionRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Chapter.ChapterRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Chapter.ChapterUploadRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.FILE.FileRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.FILE.ImageFileRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.FILE.RawFileRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Chapter.ChapterResponseDetail;
import com.vawndev.spring_boot_readnovel.Dto.Responses.FileResponse;
import com.vawndev.spring_boot_readnovel.Entities.*;
import com.vawndev.spring_boot_readnovel.Enum.STORY_STATUS;
import com.vawndev.spring_boot_readnovel.Enum.StoryType;
import com.vawndev.spring_boot_readnovel.Enum.TransactionType;
import com.vawndev.spring_boot_readnovel.Exceptions.AppException;
import com.vawndev.spring_boot_readnovel.Exceptions.ErrorCode;
import com.vawndev.spring_boot_readnovel.Repositories.*;
import com.vawndev.spring_boot_readnovel.Services.ChapterService;
import com.vawndev.spring_boot_readnovel.Services.CloundService;
import com.vawndev.spring_boot_readnovel.Services.HistoryReadingService;
import com.vawndev.spring_boot_readnovel.Utils.FileUpload;
import com.vawndev.spring_boot_readnovel.Utils.Help.TokenHelper;
import com.vawndev.spring_boot_readnovel.Utils.Help.UserHelper;
import com.vawndev.spring_boot_readnovel.Utils.JwtUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.Authenticator;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChapterServiceImpl implements ChapterService {

    private final CloundService cloundService;
    private final StoryRepository storyRepository;
    private final ChapterRepository chapterRepository;
    private final FileRepository fileRepository;
    private final TokenHelper tokenHelper;
    private final HistoryReadingService readingService;
    private final JwtUtils jwtUtils;
    private final ReadingHistoryRepository readingHistoryRepository;
    private final UserRepository userRepository;
    private final PurchaseHistoryRepository purchaseHistoryRepository;

    public static String convertToImagePath(String cloudinaryUrl) {
        String[] parts = cloudinaryUrl.split("/");
        String fileName = parts[parts.length - 1];
        return "/images/" + fileName;
    }

    private User getAuthenticatedUser() {
        return tokenHelper.getUserO2Auth();
    }

    @Override
    @PreAuthorize("hasAuthority('AUTHOR')")
    public String addChapter(ChapterUploadRequest chapterUploadRequest, List<MultipartFile> uploadedFiles) {
        ChapterRequest creq = chapterUploadRequest.getChapter();
        User Auth = getAuthenticatedUser();

        List<MultipartFile> images = new ArrayList<>();
        List<MultipartFile> files = new ArrayList<>();
        Story story = storyRepository.findByIdAndAuthor(chapterUploadRequest.getChapter().getStory_id(), Auth.getId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Story not found"));
        if (uploadedFiles != null && !uploadedFiles.isEmpty()) {
            for (MultipartFile file : uploadedFiles) {
                if (file.getContentType() != null && file.getContentType().startsWith("image/")
                        && story.getType().equals(StoryType.COMIC)) {
                    images.add(file);
                } else {
                    files.add(file);
                }
            }
        }
        if (!files.isEmpty()) {
            RawFileRequest rawUpload = new RawFileRequest();
            rawUpload.setFile(files);
            chapterUploadRequest.setFile(rawUpload);
        }

        if (!images.isEmpty()) {
            ImageFileRequest imageUpload = new ImageFileRequest();
            imageUpload.setFile(images);
            chapterUploadRequest.setFile(imageUpload);
        }

        List<String> listUrl;
        try {
            FileRequest freq = chapterUploadRequest.getFile();
            listUrl = cloundService.getUrlChapterAfterUpload(freq);
        } catch (IOException e) {
            throw new AppException(ErrorCode.SERVER_ERROR);
        }
        Chapter chapter = Chapter.builder()
                .title("Chương " + (storyRepository.countChapters(story.getId()) + 1))
                .content(creq.getContent())
                .views(0L)
                .price(creq.getPrice())
                .story(story)
                .build();
        if (story.getStatus().equals(STORY_STATUS.UPDATING)) {
            story.setStatus(STORY_STATUS.UPDATING);
        }
        story.setUpdatedAt(Instant.now());
        Chapter savedChapter = chapterRepository.save(chapter);
        storyRepository.save(story);

        List<File> imageList = listUrl.stream()
                .map(url -> File.builder()
                        .url(url)
                        .chapter(savedChapter)
                        .build())
                .collect(Collectors.toList());

        fileRepository.saveAll(imageList);
        return chapter.getId();
    }

    @Override
    @PreAuthorize("hasAuthority('AUTHOR') or hasAuthority('ADMIN')")
    @Transactional

    public void deleteChapter(String id) {
        try {
            User user = getAuthenticatedUser();
            Chapter chapter = getChapterByIdAndPermissions(id, user);

            List<File> files = fileRepository.findByChapterId(chapter.getId());
            List<String> publicIds = extractPublicIds(files);

            try {
                removeFilesFromCloud(publicIds);
            } catch (Exception e) {
                throw new AppException(ErrorCode.SERVER_ERROR, "Lỗi khi xóa file trên Cloud: " + e.getMessage());
            }

            Story story = getStoryByChapter(chapter);

            deleteRelatedEntities(chapter, files);

            chapterRepository.delete(chapter);
            storyRepository.save(story);
        } catch (AppException e) {
            throw e; // Re-throw to handle specific application-level errors
        } catch (Exception e) {
            throw new AppException(ErrorCode.SERVER_ERROR, "Unexpected error: " + e.getMessage());
        }

    }

    private Chapter getChapterByIdAndPermissions(String id, User user) {
        if (user.getRoles().contains("ADMIN")) {
            return chapterRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrorCode.INVALID_CHAPTER));
        } else {
            return chapterRepository.findByIdAndAuthorId(id, user.getId())
                    .orElseThrow(() -> new AppException(ErrorCode.INVALID_CHAPTER));
        }
    }

    private List<String> extractPublicIds(List<File> files) {
        return files.stream()
                .map(file -> FileUpload.extractPublicId(file.getUrl()))
                .collect(Collectors.toList());
    }

    private void removeFilesFromCloud(List<String> publicIds) {
        try {
            cloundService.removeUrlOnChapterDelete(publicIds);
        } catch (Exception e) {
            throw new AppException(ErrorCode.SERVER_ERROR, "Lỗi khi xóa file trên Cloud: " + e.getMessage());
        }
    }

    private Story getStoryByChapter(Chapter chapter) {
        return storyRepository.findById(chapter.getStory().getId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Story not found"));
    }

    public void deleteRelatedEntities(Chapter chapter, List<File> files) {
        readingHistoryRepository.deleteAllByChapterId(chapter.getId());
        fileRepository.deleteAll(files);
    }

    @Override
    @Transactional
    public ChapterResponseDetail getChapterDetail(String id) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CHAPTER));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = null;

        if (authentication != null) {
            user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "User not found"));
        }
        User userfinal = user;
        boolean isAuthor = userfinal != null && userfinal.getId().equals(chapter.getStory().getAuthor().getId());
        if (chapter.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            if (!isAuthor) {
                if (userfinal == null || userfinal.getSubscription() == null) {
                    throw new AppException(ErrorCode.CAPITAL,
                            "You must upgrade your account or buy to read this chapter");
                }
            }
        }

        if (userfinal != null) {
            readingService.saveHistory(chapter.getId());

        }
        String next = chapterRepository.findNextChapter(id, PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElse(null);
        String prev = chapterRepository.findPrevChapter(id, PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElse(null);

        return ChapterResponseDetail.builder()
                .id(chapter.getId())
                .title(chapter.getTitle())
                .content(chapter.getContent())
                .price(UserHelper.getPriceByUser(chapter.getPrice(), user))
                .transactionType(TransactionType.DEPOSIT)
                .views(Objects.requireNonNullElse(chapter.getViews(), 0L))
                .next(next)
                .prev(prev)
                .files(chapter.getFiles().stream()
                        .map(file -> FileResponse.builder().id(file.getId()).build())
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public void buyChapter(ConditionRequest req) {
        User user = getAuthenticatedUser();
        Chapter chapter = chapterRepository.findById(req.getId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Chapter"));

        Optional<PurchaseHistory> existingPurchase = purchaseHistoryRepository.findByChapterAndUser(chapter.getId(),
                user.getId());
        if (existingPurchase.isPresent()) {
            throw new AppException(ErrorCode.CONFLICT, "You already buy this!!");
        }

        // Tạo mới PurchaseHistory nếu chưa mua
        PurchaseHistory purchaseHistory = PurchaseHistory.builder()
                .chapter(chapter)
                .user(user)
                .build();

        if (user.getId().equals(chapter.getStory().getAuthor().getId())) {
            throw new AppException(ErrorCode.CONFLICT, "You are author!!");
        }

        if (user.getBalance().subtract(chapter.getPrice()).compareTo(BigDecimal.ZERO) < 0) {
            throw new AppException(ErrorCode.FAILED_PAYMENT, "Your balance is not sufficient to use this method");
        }

        BigDecimal result = user.getBalance().subtract(chapter.getPrice());
        user.setBalance(result);
        userRepository.save(user);

        // Lưu PurchaseHistory
        purchaseHistoryRepository.save(purchaseHistory);
    }

}
