package com.donation.ddb.Domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long pId;

    @Column(nullable = false, length = 100)
    private String pTitle;

    @Column(nullable = false, length = 5000)
    private String pContent;

    @Column(length = 500)
    private String pNft;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sId")
    private StudentUser studentUser;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostLike> postLikeList;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostComment> postCommentList;
}
