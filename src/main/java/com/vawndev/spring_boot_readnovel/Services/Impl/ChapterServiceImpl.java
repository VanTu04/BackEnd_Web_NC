package com.vawndev.spring_boot_readnovel.Services.Impl;

import com.vawndev.spring_boot_readnovel.Dto.Requests.Chapter.ChapterRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Chapter.ChapterUploadRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.FILE.FileRequest;
import com.vawndev.spring_boot_readnovel.Entities.Chapter;
import com.vawndev.spring_boot_readnovel.Entities.Image;
import com.vawndev.spring_boot_readnovel.Entities.Story;
import com.vawndev.spring_boot_readnovel.Exceptions.AppException;
import com.vawndev.spring_boot_readnovel.Exceptions.ErrorCode;
import com.vawndev.spring_boot_readnovel.Repositories.ChapterRepository;
import com.vawndev.spring_boot_readnovel.Repositories.ImageRepository;
import com.vawndev.spring_boot_readnovel.Repositories.StoryRepository;
import com.vawndev.spring_boot_readnovel.Services.ChapterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChapterServiceImpl implements ChapterService {

    private final CloundServiceImpl cloundServiceImpl;
    private final StoryRepository storyRepository;
    private final ChapterRepository chapterRepository;
    private final ImageRepository imageRepository;



    @Override
    public void addChapter(ChapterUploadRequest chapterUploadRequest ) {
        FileRequest freq =chapterUploadRequest.getFile();
        ChapterRequest creq=chapterUploadRequest.getChapter();
       try{
           Story story = storyRepository.findById(creq.getStory_id())
                   .orElseThrow(() -> new AppException(ErrorCode.INVALID_STORY));

           List<String> listUrl;
           try {
               listUrl = cloundServiceImpl.getUrlAfterUpload(freq);
           } catch (IOException e) {
               throw new RuntimeException("Error upload to Cloudinary", e);
           }

           Chapter chapter = Chapter.builder()
                   .price(creq.getPrice())
                   .story(story)
                   .build();
           Chapter savedChapter = chapterRepository.save(chapter);

           List<Image> imageList = new ArrayList<>();
           for (String url : listUrl) {
               Image image = Image.builder()
                       .url(url)
                       .chapter(savedChapter)
                       .build();
               imageList.add(image);
           }

           imageRepository.saveAll(imageList);
       } catch (Exception e) {
           throw new AppException(ErrorCode.SERVER_ERROR);
       }
    }

    @Override
    public void deleteChapter(String id) {
        Chapter chapter=chapterRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.INVALID_STORY));
        chapterRepository.delete(chapter);
    }
}
