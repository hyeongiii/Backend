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
    VALIDATION_ERROR("R001", HttpStatus.BAD_REQUEST, "Validation error"),;

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
