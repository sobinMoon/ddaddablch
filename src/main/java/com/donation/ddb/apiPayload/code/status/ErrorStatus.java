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

    _FORBIDDEN_ORGANIZATION(HttpStatus.FORBIDDEN, "COMMON4031", "단체 계정으로 접근할 수 없습니다."),
    _FORBIDDEN_STUDENT(HttpStatus.FORBIDDEN, "COMMON4032", "학생 계정으로 접근할 수 없습니다."),

    PAGE_SIZE_INVALID(HttpStatus.BAD_REQUEST, "PAGE4001", "페이지 사이즈는 0 이상이어야 합니다."),
    PAGE_NUMBER_INVALID(HttpStatus.BAD_REQUEST, "PAGE4002", "페이지 번호가 유효하지 않습니다."),

    CAMPAIGN_NOT_FOUND(HttpStatus.NOT_FOUND, "CAMPAIGN4001", "캠페인이 없습니다."),
    CAMPAIGN_INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "CAMPAIGN4002", "유효하지 않은 캠페인 카테고리입니다."),
    CAMPAIGN_INVALID_STATUS_FLAG(HttpStatus.BAD_REQUEST, "CAMPAIGN4003", "유효하지 않은 캠페인 상태입니다."),
    CAMPAIGN_INVALID_SORT_TYPE(HttpStatus.BAD_REQUEST, "CAMPAIGN4004", "유효하지 않은 캠페인 정렬 타입입니다."),
    CAMPAIGN_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "CAMPAIGN4005", "캠페인이 완료되지 않았습니다."),
    CAMPAIGN_INVALID_STATUS_UPDATE(HttpStatus.BAD_REQUEST, "CAMPAIGN4006", "캠페인 상태가 올바른 순서로 변경되지 않았습니다."),
    CAMPAIGN_PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "CAMPAIGN_PLAN4001", "캠페인 플랜이 없습니다."),

    CAMPAIGN_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "CAMPAIGN_COMMENT4001", "캠페인 댓글이 없습니다."),
    CAMPAIGN_COMMENT_LIKE_SELF(HttpStatus.BAD_REQUEST, "CAMPAIGN_COMMENT4002", "본인 댓글에는 좋아요할 수 없습니다."),

    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "POST4001", "게시글이 없습니다."),
    POST_LIKE_SELF(HttpStatus.BAD_REQUEST, "POST4002", "본인 게시글에는 좋아요할 수 없습니다."),
    POST_DELETE_FORBIDDEN(HttpStatus.FORBIDDEN, "POST4003", "타인의 게시글을 삭제할 수 없습니다."),

    POST_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "POST_COMMENT4001", "게시글 댓글이 없습니다."),
    POST_COMMENT_LIKE_SELF(HttpStatus.BAD_REQUEST, "POST_COMMENT4002", "본인 댓글에는 좋아요할 수 없습니다."),

    STUDENT_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "STUDENT_USER4001", "학생 사용자가 없습니다."),

    ORGANIZATION_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORGANIZATION_USER4001", "단체 사용자가 없습니다."),

    // 기부 관련 에러 코드 추가
    DONATION_MISSING_TRANSACTION_HASH(HttpStatus.BAD_REQUEST, "DONATION4001", "트랜잭션 해시가 필요합니다."),
    DONATION_INVALID_TRANSACTION(HttpStatus.BAD_REQUEST, "DONATION4002", "유효하지 않은 트랜잭션입니다. 블록체인에서 해당 트랜잭션을 찾을 수 없습니다."),
    DONATION_DUPLICATE_TRANSACTION(HttpStatus.CONFLICT, "DONATION4003", "이미 기록된 트랜잭션입니다."),
    DONATION_NOT_FOUND(HttpStatus.NOT_FOUND, "DONATION4004", "해당 해시의 기부 기록을 찾을 수 없습니다."),
    DONATION_INVALID_STATUS(HttpStatus.BAD_REQUEST, "DONATION4005", "지원하지 않는 기부 상태값입니다."),
    DONATION_MISSING_REQUIRED_FIELDS(HttpStatus.BAD_REQUEST, "DONATION4006", "필수 입력값이 누락되었습니다."),
    DONATION_RECORD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "DONATION5001", "기부 기록 저장에 실패했습니다."),
    DONATION_STATUS_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "DONATION5002", "기부 상태 업데이트에 실패했습니다."),

    // 블록체인 관련 에러 코드
    BLOCKCHAIN_CONNECTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "BLOCKCHAIN5001", "블록체인 연결에 실패했습니다."),
    BLOCKCHAIN_BALANCE_QUERY_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "BLOCKCHAIN5002", "잔액 조회 중 오류가 발생했습니다."),
    BLOCKCHAIN_TRANSACTION_VERIFICATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "BLOCKCHAIN5003", "트랜잭션 검증 중 오류가 발생했습니다."),
    ORGANIZATION_MYPAGE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"MYPAGE4001","조직 마이페이지 정보를 불러올 수 없습니다.");
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
