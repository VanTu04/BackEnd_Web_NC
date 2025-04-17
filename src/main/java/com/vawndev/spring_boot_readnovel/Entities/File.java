package com.vawndev.spring_boot_readnovel.Entities;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "File")
public class File extends BaseEntity {
    private String url;
    @ManyToOne
    @JoinColumn(name = "chapter_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Chapter chapter;
}
