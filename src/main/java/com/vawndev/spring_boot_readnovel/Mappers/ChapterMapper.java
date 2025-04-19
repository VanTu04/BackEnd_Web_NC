package com.vawndev.spring_boot_readnovel.Mappers;

import com.vawndev.spring_boot_readnovel.Dto.Responses.Chapter.ChapterResponseDetail;
import com.vawndev.spring_boot_readnovel.Entities.Chapter;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChapterMapper {
    ChapterResponseDetail toChapterResponses(Chapter chapter);
}
