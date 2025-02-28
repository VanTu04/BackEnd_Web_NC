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

import com.vawndev.spring_boot_readnovel.Dto.Requests.StoryRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.StoryResponse;
import com.vawndev.spring_boot_readnovel.Services.StoryService;

@RestController
@RequestMapping("/api/stories")
public class StoryController {
    @Autowired
    private StoryService storyService;

    @PostMapping
    public StoryResponse addStory(@RequestBody StoryRequest story) {
        return storyService.addStory(story);
    }

    @PutMapping("/{id}")
    public StoryResponse updateStory(@PathVariable String id, @RequestBody StoryRequest storyDetails) {
        return storyService.updateStory(id, storyDetails);
    }

    @DeleteMapping("/{id}")
    public void deleteStory(@PathVariable String id) {
        storyService.deleteStory(id);
    }

    @GetMapping
    public List<StoryResponse> getAllStories() {
        return storyService.getAllStories();
    }
}