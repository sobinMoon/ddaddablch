package com.donation.ddb.Repository.PostRepository;

import com.donation.ddb.Dto.Response.StudentMyPageResponseDTO;
import com.donation.ddb.Repository.projection.PostWithCount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostRepositoryCustom {
    Page<PostWithCount> findPostListCustom(Pageable pageable);
    PostWithCount findPostWithCountByPId(Long postId);

    //마이페이지용 메서드 추가
    List<StudentMyPageResponseDTO.RecentPostDTO> findRecentPostsByStudentId(Long sId);
}
