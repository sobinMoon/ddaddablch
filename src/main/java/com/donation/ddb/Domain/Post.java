package com.donation.ddb.Domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Builder
@Getter
@Setter
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

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "pNft")
    private String pNft;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sId")
    private StudentUser studentUser;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> postLikeList;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostComment> postCommentList;
}
