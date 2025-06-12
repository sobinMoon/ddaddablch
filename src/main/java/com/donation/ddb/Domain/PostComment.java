package com.donation.ddb.Domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostComment extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pcId;

    @Column(nullable = false, length = 500)
    private String pcContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pId")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sId")
    private StudentUser studentUser;

    @OneToMany(mappedBy = "postComment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostCommentLike> postCommentLikeList;
}
