package com.vawndev.spring_boot_readnovel.Dto.Responses.Category;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vawndev.spring_boot_readnovel.Entities.Category;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoriesResponse {
    private List<CategoryResponse> categories;
}
