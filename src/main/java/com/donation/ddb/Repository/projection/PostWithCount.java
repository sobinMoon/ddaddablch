package com.donation.ddb.Repository.projection;

import com.donation.ddb.Domain.StudentUser;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class PostWithCount {
    private Long postId;
    private String title;
    private String previewContent;
    private String nft;
    private Long likeCount;
    private Long commentCount;
    private StudentUser studentUser;
    private LocalDateTime createdAt;
}
