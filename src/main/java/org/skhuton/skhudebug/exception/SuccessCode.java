package org.skhuton.skhudebug.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum SuccessCode {

    /*201 CREATED*/
    BUGHUNT_CREATE_SUCCESS(HttpStatus.CREATED, "헌터 구인 요청 완료"),


    /*200 OK*/
    RANKING_GET_SUCCESS(HttpStatus.OK, "랭킹 조회 성공"),
    GET_SUCCESS(HttpStatus.OK, "조회 완료"),
    BUGHUNT_RADIUS_GET_SUCCESS(HttpStatus.OK, "조회 완료"),
    BUGHUNT_REQUEST_ACCEPT(HttpStatus.OK, "헌터 구인 요청 수락 완료"),
    BUGHUNT_REQUEST_COMPLETE(HttpStatus.OK, "벌레 제거 완료")
    ;

    private final HttpStatus httpStatus;
    private final String message;

    public int getHttpStatusCode(){
        return httpStatus.value();
    }
}