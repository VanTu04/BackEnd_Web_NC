package com.vawndev.spring_boot_readnovel.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "chapters")
@SuperBuilder
public class Chapter extends BaseEntity {
    private String title;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Story story;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "views")
    private Long views;

    private BigDecimal price;

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<File> files;
}
