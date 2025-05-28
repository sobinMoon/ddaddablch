package com.donation.ddb.Dto.Response;

import com.donation.ddb.Domain.Donation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonationResponseDTO {

    private Long id;
    private String transactionHash;
    private BigDecimal amount;
    private String donorName;
    private String donorNickname;

    private String campaignName;
    private String message;
    //private Boolean isAnonymous;
    private String status;


    public static DonationResponseDTO from(Donation donation) {
        return DonationResponseDTO.builder()
                .id(donation.getId())
                .transactionHash(donation.getTransactionHash())
                .amount(donation.getAmount())
                //.donorName(donation.getIsAnonymous() ? "익명" : donation.getStudentUser().getSName())
                .donorName(donation.getStudentUser().getSEmail())
                .donorNickname(donation.getStudentUser().getSNickname())
                .campaignName(donation.getCampaign() != null ? donation.getCampaign().getCName() : null)
                .message(donation.getMessage())
                //.isAnonymous(donation.getIsAnonymous())
                .status(donation.getStatus().toString())
                //.createdAt(donation.getCreatedAt())
                .build();
    }


}

