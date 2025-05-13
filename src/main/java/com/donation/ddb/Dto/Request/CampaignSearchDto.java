package com.donation.ddb.Dto.Request;

import com.donation.ddb.Domain.CampaignCategory;
import com.donation.ddb.Domain.CampaignSortType;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CampaignSearchDto {
    private String searchType; // 검색 타입 (제목, 내용 등)
    private String searchKeyword; // 검색 키워드
    private CampaignCategory category; // 카테고리 (예: 환경, 동물 등)
    private CampaignSortType sortType; // 정렬 타입 (최신순, 인기순 등)
    private int page; // 페이지 번호
    private int size; // 페이지 크기
}
