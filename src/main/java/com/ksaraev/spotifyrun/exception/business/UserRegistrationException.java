package com.ksaraev.spotifyrun.exception.business;

import lombok.experimental.StandardException;

@StandardException
public class UserRegistrationException extends RuntimeException {
    public static final String UNABLE_TO_REGISTER_USER = "Unable to register user: ";
}
