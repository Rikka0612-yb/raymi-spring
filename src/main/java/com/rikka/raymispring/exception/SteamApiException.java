package com.rikka.raymispring.exception;

import com.rikka.raymispring.constant.ErrorCodeConstants;
import lombok.Getter;

@Getter
public class SteamApiException extends BusinessException {
    private final int httpStatus;
    private final Integer eresult;

    public SteamApiException(String message, int httpStatus, Integer eresult) {
        super(ErrorCodeConstants.valueOf(message));
        this.httpStatus = httpStatus;
        this.eresult = eresult;
    }

}
