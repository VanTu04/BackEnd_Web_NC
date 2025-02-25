package com.vawndev.spring_boot_readnovel.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "chapters")
@Builder
public class Chapter extends BaseEntity {
    private String title;

    @ManyToOne
    private Story story;

    private String content;

    private BigDecimal price;

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images;
}
