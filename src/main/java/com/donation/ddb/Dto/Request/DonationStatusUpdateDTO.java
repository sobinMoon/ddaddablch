package com.donation.ddb.Dto.Request;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DonationStatusUpdateDTO {
    @NotBlank(message = "트랜잭션 해시는 필수입니다")
    private String transactionHash;

    @NotBlank(message = "상태는 필수입니다")
    private String status;
}
