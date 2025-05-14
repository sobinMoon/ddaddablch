package com.donation.ddb.apiPayload.code.status;

import com.donation.ddb.apiPayload.code.BaseErrorCode;
import com.donation.ddb.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),


    CAMPAIGN_NOT_FOUND(HttpStatus.NOT_FOUND, "CAMPAIGN4001", "캠페인이 없습니다."),
    CAMPAIGN_INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "CAMPAIGN4002", "유효하지 않은 캠페인 카테고리입니다."),
    CAMPAIGN_INVALID_STATUS_FLAG(HttpStatus.BAD_REQUEST, "CAMPAIGN4003", "유효하지 않은 캠페인 상태입니다."),
    CAMPAIGN_INVALID_SORT_TYPE(HttpStatus.BAD_REQUEST, "CAMPAIGN4004", "유효하지 않은 캠페인 정렬 타입입니다."),

    CAMPAIGN_PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "CAMPAIGN_PLAN4001", "캠페인 플랜이 없습니다."),

    ORGANIZATION_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORGANIZATION_USER4001", "단체 사용자가 없습니다.");


    // 예시,,,
    // ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "ARTICLE4001", "게시글이 없습니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build()
                ;
    }
}
