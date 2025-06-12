package com.donation.ddb.Dto.Response;

import com.donation.ddb.Domain.Donation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonationResponseDTO {
    private Long id;
}

