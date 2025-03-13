package com.vawndev.spring_boot_readnovel.Dto.Responses.Chapter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vawndev.spring_boot_readnovel.Dto.Responses.FileResponse;

import com.vawndev.spring_boot_readnovel.Enum.TransactionType;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChapterResponses {
    private String id;

    private String title;

    private String content;

    private BigDecimal price;

    private TransactionType transactionType;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<FileResponse> files;

    public BigDecimal getPrice() {
        return (this.transactionType == TransactionType.PURCHASE) ? BigDecimal.ZERO : (this.price != null ? this.price : BigDecimal.ZERO);
    }


}
