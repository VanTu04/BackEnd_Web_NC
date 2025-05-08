package com.vawndev.spring_boot_readnovel.Entities;

import com.vawndev.spring_boot_readnovel.Enum.IS_AVAILBLE;
import com.vawndev.spring_boot_readnovel.Enum.STORY_STATUS;
import com.vawndev.spring_boot_readnovel.Enum.StoryType;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "stories")
public class StoryDocument {
    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Integer)
    private Long views;

    @Field(type = FieldType.Keyword)
    private StoryType type;

    @Field(type = FieldType.Keyword)
    private STORY_STATUS status;

    @Field(type = FieldType.Keyword)
    private IS_AVAILBLE isAvailable;

    @Field(type = FieldType.Boolean)
    private Boolean isVisibility;

    @Field(type = FieldType.Boolean)
    private Boolean isBanned;

    @Field(type = FieldType.Double)
    private Double rate;

    @Field(type = FieldType.Text)
    private List<String> categories;

    @Field(type = FieldType.Text)
    private String authorName;

    @Field(type = FieldType.Keyword)
    private String authorEmail;
}
