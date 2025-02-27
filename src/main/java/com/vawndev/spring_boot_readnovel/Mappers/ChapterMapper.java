package com.vawndev.spring_boot_readnovel.Mappers;

import com.vawndev.spring_boot_readnovel.Dto.Requests.ChapterRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.ChapterResponse;
import com.vawndev.spring_boot_readnovel.Entities.Chapter;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ChapterMapper {
    Chapter toEntity(ChapterRequest chapterRequest);

    ChapterResponse toResponse(Chapter chapter);

    void updateEntity(ChapterRequest chapterRequest, @MappingTarget Chapter chapter);
}