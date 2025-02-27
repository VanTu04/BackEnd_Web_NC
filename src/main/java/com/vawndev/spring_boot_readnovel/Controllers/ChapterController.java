package com.vawndev.spring_boot_readnovel.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vawndev.spring_boot_readnovel.Entities.Chapter;
import com.vawndev.spring_boot_readnovel.Services.ChapterService;

@RestController
@RequestMapping("/api/chapters")
public class ChapterController {
    @Autowired
    private ChapterService chapterService;

    @PostMapping("/{storyId}")
    public Chapter addChapter(@PathVariable String storyId, @RequestBody Chapter chapter) {
        return chapterService.addChapter(storyId, chapter);
    }

    @PutMapping("/{id}")
    public Chapter updateChapter(@PathVariable String id, @RequestBody Chapter chapterDetails) {
        return chapterService.updateChapter(id, chapterDetails);
    }
/* 
    @DeleteMapping("/{id}")
    public void deleteChapter(@PathVariable String id) {
        chapterService.deleteChapter(id);
    }
*/
    @GetMapping
    public List<Chapter> getAllChapters() {
        return chapterService.getAllChapters();
    }
}