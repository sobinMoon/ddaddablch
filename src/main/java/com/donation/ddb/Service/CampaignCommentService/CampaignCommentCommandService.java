package com.donation.ddb.Service.CampaignCommentService;

import com.donation.ddb.Domain.CampaignComment;
import com.donation.ddb.Repository.CampaignCommentRepostory.CampaignCommentRepository;
import com.donation.ddb.Repository.CampaignRepository.CampaignRepository;
import com.donation.ddb.Repository.StudentUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CampaignCommentCommandService {
    private final CampaignRepository campaignRepository;
    private final CampaignCommentRepository campaignCommentRepository;
    private final StudentUserRepository studentUserRepository;

    public CampaignComment addComment(String content, Long cId, String userEmail) {
        CampaignComment campaignComment = CampaignComment.builder()
                .ccContent(content)
                .studentUser(studentUserRepository.findBysEmail(userEmail).orElseThrow(() -> new IllegalArgumentException("User not found")))
                .campaign(campaignRepository.findBycId(cId))
                .build();
        campaignCommentRepository.save(campaignComment);

        return campaignComment;
    }

}
