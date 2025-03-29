package com.vawndev.spring_boot_readnovel.Entities;

import com.vawndev.spring_boot_readnovel.Enum.IS_AVAILBLE;
import com.vawndev.spring_boot_readnovel.Enum.STORY_STATUS;
import com.vawndev.spring_boot_readnovel.Enum.StoryType;
import jakarta.persistence.*;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;
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

    private Long views;

    @Enumerated(EnumType.STRING)
    private StoryType type;

    @ManyToOne
    private User author;

    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_available")
    private IS_AVAILBLE isAvailable;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private STORY_STATUS status;

    @Column(name = "is_visibility")
    private boolean isVisibility;

    @Column(name="is_banned")
    private boolean isBanned;

    private double rate;

    @ManyToMany
    @JoinTable(
            name = "story_categories",
            joinColumns = @JoinColumn(name = "story_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;

    @JsonIgnore
    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Chapter> chapters;

    @Column(name = "cover_image")
    private String coverImage;
}
