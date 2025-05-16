package com.donation.ddb.Service.CampaignCommentQueryService;

import com.donation.ddb.Domain.CampaignComment;
import com.donation.ddb.Dto.Response.CampaignCommentListResponseDto;
import com.donation.ddb.Dto.Response.CampaignCommentResponseDto;
import com.donation.ddb.Repository.CampaignCommentLikeRepository.CampaignCommentLikeRepository;
import com.donation.ddb.Repository.CampaignCommentRepostory.CampaignCommentRepository;
import com.donation.ddb.Repository.projection.CommentLikeCount;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CampaignCommentQueryService {
    private final CampaignCommentRepository campaignCommentRepository;
    private final CampaignCommentLikeRepository campaignCommentLikeRepository;

    public CampaignCommentListResponseDto findCommentByCampaignId(Long cId, Pageable pageable) {
        Long count = campaignCommentRepository.countByCampaign_cId(cId);

        List<CampaignComment> commentEntities = campaignCommentRepository.findByCampaign_cId(cId, pageable);
        List<Long> commentIds = commentEntities.stream()
                .map(CampaignComment::getCcId)
                .toList();

        // 좋아요 수를 일괄 조회
        Map<Long, Long> likesMap = campaignCommentLikeRepository.countLikesByCommentIds(commentIds).stream()
                .collect(Collectors.toMap(CommentLikeCount::getCommentId, CommentLikeCount::getCount));

        // DTO로 변환 시 좋아요 수 전달
        List<CampaignCommentResponseDto> comments = commentEntities.stream()
                .map(comment -> CampaignCommentResponseDto.from(
                        comment,
                        likesMap.getOrDefault(comment.getCcId(), 0L)
                ))
                .toList();

        return CampaignCommentListResponseDto.from(count, comments);
    }
}
