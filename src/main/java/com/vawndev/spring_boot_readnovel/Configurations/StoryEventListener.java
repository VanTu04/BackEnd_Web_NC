package com.vawndev.spring_boot_readnovel.Configurations;

import com.vawndev.spring_boot_readnovel.Entities.Category;
import com.vawndev.spring_boot_readnovel.Entities.Story;
import com.vawndev.spring_boot_readnovel.Entities.StoryDocument;
import com.vawndev.spring_boot_readnovel.Repositories.StoryDocumentRepository;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StoryEventListener {
    private final StoryDocumentRepository storyDocumentRepository;

    @PostPersist
    @PostUpdate
    public void handleStoryPostPersist(Story story) {
        StoryDocument esStory = StoryDocument.builder()
                .id(story.getId())
                .title(story.getTitle())
                .description(story.getDescription())
                .views(story.getViews())
                .type(story.getType())
                .authorName(story.getAuthor() != null ? story.getAuthor().getFullName() : null)
                .authorEmail(story.getAuthor() != null ? story.getAuthor().getEmail(): null)
                .isAvailable(story.getIsAvailable())
                .status(story.getStatus())
                .isVisibility(story.isVisibility())
                .isBanned(story.isBanned())
                .rate(story.getRate())
                .categories(story.getCategories().stream().map(Category::getName).toList())
                .build();
        storyDocumentRepository.save(esStory);
    }
}
