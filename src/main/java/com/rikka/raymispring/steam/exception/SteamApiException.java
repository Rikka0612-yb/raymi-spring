package com.rikka.raymispring.steam.exception;

import lombok.Getter;

@Getter
public class SteamApiException extends RuntimeException {
    private final int httpStatus;
    private final Integer eresult;

    public SteamApiException(String message, int httpStatus, Integer eresult) {
        super(message);
        this.httpStatus = httpStatus;
        this.eresult = eresult;
    }

}
