package com.ksaraev.spotifyrun.exception.business;

import lombok.experimental.StandardException;

@StandardException
public class AuthenticationException extends RuntimeException {

  public static final String USER_IS_NOT_AUTHENTICATED = "User is not authenticated";
  public static final String UNABLE_TO_GET_USER_AUTHENTICATION_DETAILS = "Unable to get user authentication details: ";
  public static final String UNKNOWN_TYPE_OF_PRINCIPAL = "UNKNOWN_TYPE_OF_PRINCIPAL";
}
