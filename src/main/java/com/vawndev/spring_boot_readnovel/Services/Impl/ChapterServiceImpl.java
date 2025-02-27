package com.vawndev.spring_boot_readnovel.Services.Impl;

import com.vawndev.spring_boot_readnovel.Entities.Chapter;
import com.vawndev.spring_boot_readnovel.Entities.Story;
import com.vawndev.spring_boot_readnovel.Exceptions.AppException;
import com.vawndev.spring_boot_readnovel.Exceptions.ErrorCode;
import com.vawndev.spring_boot_readnovel.Repositories.ChapterRepository;
import com.vawndev.spring_boot_readnovel.Repositories.StoryRepository;
import com.vawndev.spring_boot_readnovel.Services.ChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChapterServiceImpl implements ChapterService {
    @Autowired
    private ChapterRepository chapterRepository;

    @Autowired
    private StoryRepository storyRepository;

    @Override
    public Chapter addChapter(String storyId, Chapter chapter) {
        Optional<Story> optionalStory = storyRepository.findById(storyId);
        if (optionalStory.isPresent()) {
            chapter.setStory(optionalStory.get());
            return chapterRepository.save(chapter);
        } else {
            throw new AppException(ErrorCode.DATA_NOT_FOUND);
        }
    }

    @Override
    public Chapter updateChapter(String id, Chapter chapterDetails) {
        Optional<Chapter> optionalChapter = chapterRepository.findById(id);
        if (optionalChapter.isPresent()) {
            Chapter chapter = optionalChapter.get();
            chapter.setTitle(chapterDetails.getTitle());
            chapter.setContent(chapterDetails.getContent());
            chapter.setUpdatedAt(chapterDetails.getUpdatedAt());
            chapter.setPrice(chapterDetails.getPrice());
            return chapterRepository.save(chapter);
        } else {
            throw new AppException(ErrorCode.DATA_NOT_FOUND);
        }
    }
/* 
    @Override
    public void deleteChapter(Long id) {
        Chapter existingChapter= chapterRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));
        existingChapter.set(false);
        storyRepository.save(existingChapter);
    }
*/
    @Override
    public List<Chapter> getAllChapters() {
        return chapterRepository.findAll();
    }
}