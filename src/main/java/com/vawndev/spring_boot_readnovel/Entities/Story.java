package com.vawndev.spring_boot_readnovel.Entities;

import com.vawndev.spring_boot_readnovel.Enum.StoryState;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "stories")
@DynamicUpdate
public class Story extends BaseEntity{
    private String title;

    private String description;

    private Long view;

    private String type;

    @ManyToOne
    private User author;

    private BigDecimal price;

    @Column(name = "is_available")
    private boolean isAvailable;

    @Enumerated(EnumType.STRING)
    private StoryState state;

    @Column(name = "is_approved")
    private boolean isApproved;

    private double rate;

    private int views;

    @ManyToMany
    @JoinTable(
            name = "story_categories",
            joinColumns = @JoinColumn(name = "story_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;
    @Column(name = "cover_image")
    private String coverImage;
}
