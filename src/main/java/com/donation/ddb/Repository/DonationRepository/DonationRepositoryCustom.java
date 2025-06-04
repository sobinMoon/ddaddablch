package com.donation.ddb.Repository.DonationRepository;

import com.donation.ddb.Dto.Response.DonationStatusDTO;
import com.donation.ddb.Dto.Response.StudentMyPageResponseDTO;

import java.util.List;

public interface DonationRepositoryCustom {

    //특정 학생의 기부 통계 정보 (총 기부 횟수, 총 금액 등..)
    DonationStatusDTO getDonationStatsByStudentId(Long studentId);

    //
    List<StudentMyPageResponseDTO.DonationSummaryDTO> getRecentDonationSummary(Long studentId, int limit);

    // 필요 시 구현하기
    // List<DonationHistoryDTO> getDonationHistoryWithPaging(Long studentId, int page, int size);

    //최근 가장 많이 donate한 category
    // String getMostDonatedCategory(Long studentId);

}
