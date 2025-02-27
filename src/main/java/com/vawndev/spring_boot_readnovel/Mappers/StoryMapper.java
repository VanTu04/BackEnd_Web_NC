package com.vawndev.spring_boot_readnovel.Mappers;

import com.vawndev.spring_boot_readnovel.Dto.Requests.StoryRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.StoryResponse;
import com.vawndev.spring_boot_readnovel.Entities.Story;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface StoryMapper {
    Story toEntity(StoryRequest storyRequest);

    StoryResponse toResponse(Story story);

    void updateEntity(StoryRequest storyRequest, @MappingTarget Story story);
}