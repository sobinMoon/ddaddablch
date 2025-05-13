package com.donation.ddb.Dto.Request;

import com.donation.ddb.Domain.CampaignCategory;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CampaignRequestDto {
    @JsonProperty("oId")
    private Long oId;

    @JsonProperty("cName")
    private String cName;

    @JsonProperty("cImageUrl")
    private String cImageUrl;

    @JsonProperty("cDescription")
    private String cDescription;

    @JsonProperty("cGoal")
    private Integer cGoal;

    @JsonProperty("cCategory")
    private CampaignCategory cCategory;

    @JsonProperty("donateStart")
    private LocalDate donateStart;

    @JsonProperty("donateEnd")
    private LocalDate donateEnd;

    @JsonProperty("businessStart")
    private LocalDate businessStart;

    @JsonProperty("businessEnd")
    private LocalDate businessEnd;
}
