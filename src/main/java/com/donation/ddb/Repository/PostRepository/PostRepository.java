package com.donation.ddb.Repository.PostRepository;

import com.donation.ddb.Domain.Post;
import com.donation.ddb.Repository.projection.PostWithCount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    Post findPostBypId(Long postId);

    @Query("SELECT new com.donation.ddb.Repository.projection.PostWithCount(p.pId, p.pTitle, p.pContent, p.pNft, " +
            "(SELECT CAST(COUNT(l) AS long) FROM PostLike l WHERE l.post.pId = p.pId), " +
            "(SELECT CAST(COUNT(c) AS long) FROM PostComment c WHERE c.post.pId = p.pId), " +
            "p.createdAt) " +
            "FROM Post p ORDER BY p.createdAt DESC")
    Page<PostWithCount> findPostList(Pageable pageable);
}
