package com.donation.ddb.Repository.CampaignCommentLikeRepository;

import com.donation.ddb.Domain.CampaignComment;
import com.donation.ddb.Domain.CampaignCommentLike;
import com.donation.ddb.Domain.StudentUser;
import com.donation.ddb.Repository.projection.CommentLikeCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CampaignCommentLikeRepository extends JpaRepository<CampaignCommentLike, Long> {
    @Query("SELECT ccl.campaignComment.ccId as commentId, COUNT(ccl) as count FROM CampaignCommentLike ccl WHERE ccl.campaignComment.ccId IN :commentIds GROUP BY ccl.campaignComment.ccId")
    List<CommentLikeCount> countLikesByCommentIds(@Param("commentIds") List<Long> commentIds);

    Optional<CampaignCommentLike> findByCampaignCommentAndStudentUser(CampaignComment campaignComment, StudentUser user);

    @Query("SELECT c.campaignComment.ccId FROM CampaignCommentLike c WHERE c.studentUser.sEmail = :email AND c.campaignComment.ccId IN :commentIds")
    Set<Long> findLikedCommentIdsByEmailAndCommentIds(@Param("email") String email, @Param("commentIds") List<Long> commentIds);


//    CampaignCommentLike save(CampaignCommentLike campaignCommentLike);
}
