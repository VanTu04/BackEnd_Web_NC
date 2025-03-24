package com.vawndev.spring_boot_readnovel.Dto.Responses.Chapter;

import com.vawndev.spring_boot_readnovel.Enum.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder

public class ChapterResponsePurchase extends ChaptersResponse {


    private BigDecimal price;

    private TransactionType transactionType;

}
