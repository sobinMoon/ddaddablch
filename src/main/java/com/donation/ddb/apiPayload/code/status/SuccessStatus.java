package com.donation.ddb.apiPayload.code.status;

import com.donation.ddb.apiPayload.code.BaseCode;
import com.donation.ddb.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {

    // 일반적인 응답
    _OK(HttpStatus.OK, "COMMON200", "성공입니다."),

    //기부 관련 응답
    DONATION_RECORDED(HttpStatus.OK,"DONATION201","기부가 성공적으로 기록되었습니다."),
    DONATION_STATUS_UPDATED(HttpStatus.OK,"DONATION202","기부 상태가 성공적으로 업데이트되었습니다."),
    BALANCE_RETRIEVED(HttpStatus.OK,"DONATION203","잔액 조회가 완료되었습니다."),


    //마이페이지 관련 응답
    STUDENT_MYPAGE_INFO_RECEIVED(HttpStatus.OK,"MYPAGE201","학생 마이페이지가 성공적으로 조회되었습니다."),
    ORG_MYPAGE_INFO_RECEIVED(HttpStatus.OK,"MYPAGE202","단체 마이페이지가 성공적으로 조회되었습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDTO getReason() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .build();
    }

    @Override
    public ReasonDTO getReasonHttpStatus() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .httpStatus(httpStatus)
                .build()
                ;
    }
}
