package com.donation.ddb.Repository.PostCommentRepository;

import com.donation.ddb.Domain.PostComment;
import com.donation.ddb.Dto.Response.StudentMyPageResponseDTO;
import com.donation.ddb.Repository.projection.PostCommentWithUser;

import java.util.List;

public interface PostCommentRepositoryCustom {
    List<PostCommentWithUser> findPostListWithUser(Long postId);
    List<StudentMyPageResponseDTO.PostCommentDTO> findRecentCommentsByStudentId(Long sId);
    Long countLikesByCommentId(Long pcId);
}
