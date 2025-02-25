package com.vawndev.spring_boot_readnovel.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "reviews")
public class Review extends BaseEntity{
    @ManyToOne
    private Story story;

    @ManyToOne
    private User user;

    private int rating;

    private String comment;
}
