package com.donation.ddb.Repository.projection;

import com.donation.ddb.Domain.PostComment;
import com.donation.ddb.Domain.StudentUser;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PostCommentWithUser {
    private PostComment postComment;
    private StudentUser studentUser;
    private Long likeCount;
}
