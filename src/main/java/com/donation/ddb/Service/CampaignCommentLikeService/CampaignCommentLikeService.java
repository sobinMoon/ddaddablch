package com.donation.ddb.Service.CampaignCommentLikeService;

import com.donation.ddb.Domain.CampaignComment;
import com.donation.ddb.Domain.CampaignCommentLike;
import com.donation.ddb.Domain.StudentUser;
import com.donation.ddb.Repository.CampaignCommentLikeRepository.CampaignCommentLikeRepository;
import com.donation.ddb.Repository.CampaignCommentRepostory.CampaignCommentRepository;
import com.donation.ddb.Repository.StudentUserRepository;
import com.donation.ddb.apiPayload.code.status.ErrorStatus;
import com.donation.ddb.apiPayload.exception.handler.CampaignHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CampaignCommentLikeService {

    private final CampaignCommentLikeRepository campaignCommentLikeRepository;
    private final CampaignCommentRepository campaignCommentRepository;
    private final StudentUserRepository studentUserRepository;

    public CampaignCommentLike toggleCommentLike(Long ccId, String userEmail) {
        StudentUser user = studentUserRepository.findBysEmail(userEmail)
                .orElseThrow(() -> new CampaignHandler(ErrorStatus.STUDENT_USER_NOT_FOUND));

        CampaignComment campaignComment = campaignCommentRepository.findByCcId(ccId)
                .orElseThrow(() -> new CampaignHandler(ErrorStatus.CAMPAIGN_COMMENT_NOT_FOUND));

        if (campaignComment.getStudentUser().getSEmail().equals(userEmail)) {
            throw new CampaignHandler(ErrorStatus.CAMPAIGN_COMMENT_LIKE_SELF);
        }

        Optional<CampaignCommentLike> existingLike = campaignCommentLikeRepository.findByCampaignCommentAndStudentUser(campaignComment, user);

        CampaignCommentLike campaignCommentLike = CampaignCommentLike.builder()
                .campaignComment(campaignComment)
                .studentUser(user)
                .build();

        if (existingLike.isPresent()) {
            campaignCommentLikeRepository.delete(existingLike.get());
            return existingLike.get();
        } else {
            return campaignCommentLikeRepository.save(campaignCommentLike);
        }
    }

}
