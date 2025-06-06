package com.donation.ddb.Service.CampaignCommentService;

import com.donation.ddb.Domain.CampaignComment;
import com.donation.ddb.Domain.StudentUser;
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

import java.util.*;
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

        Map<Long, Long> likesMap = campaignCommentLikeRepository.countLikesByCommentIds(commentIds).stream()
                .collect(Collectors.toMap(CommentLikeCount::getCommentId, CommentLikeCount::getCount));

        Set<Long> likedCommentIds = new HashSet<>();

        if (userEmail != null) {
            Optional<StudentUser> studentOpt = studentUserRepository.findBysEmail(userEmail);
            if (studentOpt.isPresent()) {
                likedCommentIds = campaignCommentLikeRepository.findLikedCommentIdsByEmailAndCommentIds(userEmail, commentIds);
            } else {
                log.info("StudentUser가 아니거나 존재하지 않는 사용자: {}", userEmail);
            }
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
}