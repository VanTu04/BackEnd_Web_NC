package com.vawndev.spring_boot_readnovel.Services.Impl;

import com.vawndev.spring_boot_readnovel.Dto.Requests.StoryRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.StoryResponse;
import com.vawndev.spring_boot_readnovel.Entities.Story;
import com.vawndev.spring_boot_readnovel.Exceptions.AppException;
import com.vawndev.spring_boot_readnovel.Exceptions.ErrorCode;
import com.vawndev.spring_boot_readnovel.Mappers.StoryMapper;
import com.vawndev.spring_boot_readnovel.Repositories.StoryRepository;
import com.vawndev.spring_boot_readnovel.Services.StoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StoryServiceImpl implements StoryService {
    @Autowired
    private StoryRepository storyRepository;

    @Autowired
    private StoryMapper storyMapper;

    @Override
    public StoryResponse addStory(StoryRequest storyRequest) {
        Story story = storyMapper.toEntity(storyRequest);
        Story savedStory = storyRepository.save(story);
        return storyMapper.toResponse(savedStory);
    }

    @Override
    public StoryResponse updateStory(String id, StoryRequest storyRequest) {
        Optional<Story> optionalStory = storyRepository.findById(id);
        if (optionalStory.isPresent()) {
            Story story = optionalStory.get();
            storyMapper.updateEntity(storyRequest, story);
            Story updatedStory = storyRepository.save(story);
            return storyMapper.toResponse(updatedStory);
        } else {
            throw new AppException(ErrorCode.DATA_NOT_FOUND);
        }
    }

    @Override
    public void deleteStory(String id) {
        Story existingStory = storyRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));
        existingStory.setAvailable(false);
        storyRepository.save(existingStory);
    }

    @Override
    public List<StoryResponse> getAllStories() {
        return storyRepository.findAll().stream()
                .map(storyMapper::toResponse)
                .collect(Collectors.toList());
    }
}