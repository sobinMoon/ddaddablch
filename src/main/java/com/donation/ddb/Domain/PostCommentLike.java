package com.donation.ddb.Domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostCommentLike extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pclId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pcId")
    private PostComment postComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sId")
    private StudentUser studentUser;
}
