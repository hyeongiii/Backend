package org.balllog.backend.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.balllog.backend.global.exception.GeneralException;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

@Getter
@RequiredArgsConstructor
public enum Code {
    OK("0000", HttpStatus.OK, "SUCCESS"),

    /**
     * 서버 관련 오류
     * HEAD NAME - S (Server)
     */
    INTERNAL_ERROR("S000", HttpStatus.INTERNAL_SERVER_ERROR, "Internal error"),

    /**
     * Request 관련 오류
     * HEAD NAME - R (Request)
     */
    BAD_REQUEST("R000", HttpStatus.BAD_REQUEST, "Bad request"),
    VALIDATION_ERROR("R001", HttpStatus.BAD_REQUEST, "Validation error"),

    /**
     * 인증 관련 오류
     * HEAD NAME - AUTH (Authorization)
     */
    UNAUTHORIZED("AUTH000", HttpStatus.UNAUTHORIZED, "User unauthorized"),
    INVALID_JWT_SIGNATURE("AUTH001", HttpStatus.UNAUTHORIZED, "Jwt Signature is invalid"),
    MALFORMED_JWT("AUTH002", HttpStatus.UNAUTHORIZED, "Malformed jwt format"),
    EXPIRED_JWT("AUTH003", HttpStatus.UNAUTHORIZED, "Jwt expired. Reissue it"),
    UNSUPPORTED_JWT("AUTH004", HttpStatus.UNAUTHORIZED, "Unsupported jwt format"),
    ILLEGAL_JWT("AUTH005", HttpStatus.UNAUTHORIZED, "Illegal jwt format"),
    INVALID_REFRESH_TOKEN("AUTH006", HttpStatus.UNAUTHORIZED, "Invalid refresh token. Sign in again"),
    REFRESH_TOKEN_NOT_FOUND("AUTH007", HttpStatus.UNAUTHORIZED, "Refresh token not found. Sign in again"),

    INVALID_KAKAO_TOKEN("AUTH100", HttpStatus.UNAUTHORIZED, "Unauthorized kakao token"),


    /**
     * User 관련 오류
     * HEAD NAME - U (User)
     */
    USER_NOT_FOUND("U001", HttpStatus.NOT_FOUND, "User not found"),
    SOCIAL_TYPE_NOT_FOUND("U002", HttpStatus.NOT_FOUND, "Social type of user not found")

    ;

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;

    public String getMessage(String message) {
        return Optional.ofNullable(message)
                .filter(Predicate.not(String::isBlank))
                .orElse(this.message);
    }

    public String getMessage(Throwable e) {
        return this.getMessage(this.getMessage() + " - " + e.getMessage());
    }

    public static Code valueOf(HttpStatus httpStatus) {
        if (httpStatus == null) {
            throw new GeneralException("HttpStatus is null.");
        }

        return Arrays.stream(values())
                .filter(errorCode -> errorCode.getHttpStatus() == httpStatus)
                .findFirst()
                .orElseGet(() -> {
                    if (httpStatus.is4xxClientError()) {
                        return Code.BAD_REQUEST;
                    } else if (httpStatus.is5xxServerError()) {
                        return Code.INTERNAL_ERROR;
                    } else {
                        return Code.OK;
                    }
                });
    }
}
