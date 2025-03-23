package com.vawndev.spring_boot_readnovel.Services.Impl;

import com.vawndev.spring_boot_readnovel.Dto.Requests.Chapter.ChapterRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Chapter.ChapterUploadRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.FILE.FileRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Chapter.ChapterResponses;
import com.vawndev.spring_boot_readnovel.Dto.Responses.FileResponse;
import com.vawndev.spring_boot_readnovel.Entities.Chapter;
import com.vawndev.spring_boot_readnovel.Entities.File;
import com.vawndev.spring_boot_readnovel.Entities.Story;
import com.vawndev.spring_boot_readnovel.Entities.User;
import com.vawndev.spring_boot_readnovel.Exceptions.AppException;
import com.vawndev.spring_boot_readnovel.Exceptions.ErrorCode;
import com.vawndev.spring_boot_readnovel.Repositories.*;
import com.vawndev.spring_boot_readnovel.Services.ChapterService;
import com.vawndev.spring_boot_readnovel.Services.CloundService;
import com.vawndev.spring_boot_readnovel.Utils.FileUpload;
import com.vawndev.spring_boot_readnovel.Utils.Help.TokenHelper;
import com.vawndev.spring_boot_readnovel.Utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
public class ChapterServiceImpl implements ChapterService {

    private final CloundService cloundService;
    private final StoryRepository storyRepository;
    private final ChapterRepository chapterRepository;
    private final FileRepository fileRepository;
    private final TokenHelper tokenHelper;
    private final JwtUtils jwtUtil;
    private final UserRepository userRepository;
    private final OwnershipRepository ownershipRepository;


    @Override
//    @PreAuthorize("hasRole('AUTHOR')")
    public String addChapter(ChapterUploadRequest chapterUploadRequest,String tokenBearer) {
        FileRequest freq = chapterUploadRequest.getFile();
        ChapterRequest creq = chapterUploadRequest.getChapter();
        User Auth=tokenHelper.getRealAuthorizedUser(chapterUploadRequest.getChapter().getAuthorEmail(),tokenBearer);
        try {
            Story story = storyRepository.findByIdAndAuthor(creq.getStory_id(),Auth)
                    .orElseThrow(() -> new AppException(ErrorCode.INVALID_STORY));

            List<String> listUrl;
            try {
                listUrl = cloundService.getUrlChapterAfterUpload(freq);
            } catch (IOException e) {
                throw new RuntimeException("Error uploading to Cloudinary", e);
            }
            Chapter chapter = Chapter.builder()
                    .title(creq.getTitle())
                    .content(creq.getContent())
                    .price(creq.getPrice())
                    .story(story)
                    .build();
            Chapter savedChapter = chapterRepository.save(chapter);

            List<File> imageList = listUrl.stream()
                    .map(url -> File.builder()
                            .url(url)
                            .chapter(savedChapter)
                            .build())
                    .collect(Collectors.toList());

            fileRepository.saveAll(imageList);
            return chapter.getId();

        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @PreAuthorize("hasAnyRole('AUTHOR','ADMIN')")
    public void deleteChapter(String id, String email, String tokenBearer) {
        try {
            User user = jwtUtil.validToken(tokenHelper.getTokenInfo(tokenBearer));
            User auth;
            boolean isAuthor = user.getRoles().stream().anyMatch(role -> "AUTHOR".equals(role.getName()));
            boolean isAdmin = user.getRoles().stream().anyMatch(role -> "ADMIN".equals(role.getName()));

            if (isAuthor) {
                auth = tokenHelper.getRealAuthorizedUser(email, tokenBearer);
            } else if (isAdmin) {
                auth = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.OBJECT_NOT_EXISTED));
            } else {
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }

            Chapter chapter = chapterRepository.findByIdAndAuthorId(id, auth.getId())
                    .orElseThrow(() -> new AppException(ErrorCode.INVALID_CHAPTER));

            List<File> files = fileRepository.findByChapterId(chapter.getId());

            List<String> publicId = files.stream()
                    .map(file -> FileUpload.extractPublicId(file.getUrl()))
                    .collect(Collectors.toList());

            try {
                cloundService.removeUrlOnChapterDelete(publicId);
            } catch (Exception e) {
                throw new RuntimeException("Lỗi khi xóa file trên Cloudinary: " + e.getMessage());
            }

            fileRepository.deleteAll(files);
            chapterRepository.delete(chapter);
        } catch (Exception e) {
            throw new AppException(ErrorCode.OBJECT_NOT_EXISTED);
        }
    }



    @Override
    public ChapterResponses getChapterDetail(String id) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CHAPTER));

        // Nếu chapter có giá 0 thì ai cũng có thể xem
        if (chapter.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.OBJECT_NOT_FOUND, "User"));
            // kiểm tra xem người dùng đã mua chapter này chưa
            boolean hasOwnership = ownershipRepository.existsByUserAndChapter(currentUser, chapter);
            if (!hasOwnership) {
                throw new AppException(ErrorCode.UNAUTHORIZED, "You have not purchased this chapter");
            }
        }

        return ChapterResponses.builder()
                .id(chapter.getId())
                .title(chapter.getTitle())
                .content(chapter.getContent())
                .price(chapter.getPrice())
                .files(chapter.getFiles().stream().map(file->FileResponse.builder().id(file.getId()).build()).collect(Collectors.toList()))
                .build();
    }


}
