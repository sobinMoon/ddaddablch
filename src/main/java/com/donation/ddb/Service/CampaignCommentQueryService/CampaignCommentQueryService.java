package com.donation.ddb.Service.CampaignCommentQueryService;

import com.donation.ddb.Domain.CampaignComment;
import com.donation.ddb.Domain.StudentUser;
import com.donation.ddb.Dto.Request.CampaignCommentRequestDto;
import com.donation.ddb.Dto.Response.CampaignCommentListResponseDto;
import com.donation.ddb.Dto.Response.CampaignCommentResponseDto;
import com.donation.ddb.Repository.CampaignCommentLikeRepository.CampaignCommentLikeRepository;
import com.donation.ddb.Repository.CampaignCommentRepostory.CampaignCommentRepository;
import com.donation.ddb.Repository.CampaignRepository.CampaignRepository;
import com.donation.ddb.Repository.StudentUserRepository;
import com.donation.ddb.Repository.projection.CommentLikeCount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CampaignCommentQueryService {
    private final CampaignRepository campaignRepository;
    private final CampaignCommentRepository campaignCommentRepository;
    private final CampaignCommentLikeRepository campaignCommentLikeRepository;
    private final StudentUserRepository studentUserRepository;

    public CampaignCommentListResponseDto findCommentByCampaignId(Long cId, Pageable pageable, String userEmail) {
        Long count = campaignCommentRepository.countByCampaign_cId(cId);

        List<CampaignComment> commentEntities = campaignCommentRepository.findByCampaign_cId(cId, pageable);
        List<Long> commentIds = commentEntities.stream()
                .map(CampaignComment::getCcId)
                .toList();

        // 좋아요 수를 일괄 조회
        Map<Long, Long> likesMap = campaignCommentLikeRepository.countLikesByCommentIds(commentIds).stream()
                .collect(Collectors.toMap(CommentLikeCount::getCommentId, CommentLikeCount::getCount));

        // 유저가 좋아요를 눌렀는지 여부를 조회
        Set<Long> likedCommentIds = new HashSet<>();

        log.info(">> 요청받은 userEmail : "+userEmail);
        if (userEmail != null) {
            StudentUser user = studentUserRepository.findBysEmail(userEmail)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            likedCommentIds = campaignCommentLikeRepository.findLikedCommentIdsByEmailAndCommentIds(user.getSEmail(), commentIds);
        }

        final Set<Long> likedCommentIdsFinal = likedCommentIds;

        // DTO로 변환 시 좋아요 수 전달
        List<CampaignCommentResponseDto> comments = commentEntities.stream()
                .map(comment -> CampaignCommentResponseDto.from(
                        comment,
                        likesMap.getOrDefault(comment.getCcId(), 0L),
                        likedCommentIdsFinal.contains(comment.getCcId())

                ))
                .toList();

        return CampaignCommentListResponseDto.from(count, comments);
    }

    // 댓글추가
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
