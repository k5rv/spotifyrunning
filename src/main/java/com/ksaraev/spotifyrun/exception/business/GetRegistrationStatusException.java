package com.ksaraev.spotifyrun.exception.business;

import lombok.experimental.StandardException;

@StandardException
public class GetRegistrationStatusException extends RuntimeException {
    public static final String UNABLE_TO_GET_USER_REGISTRATION_STATUS = "Unable to get user registration status: ";
}
