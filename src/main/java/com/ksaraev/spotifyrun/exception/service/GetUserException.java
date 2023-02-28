package com.ksaraev.spotifyrun.exception.service;

import lombok.experimental.StandardException;

@StandardException
public class GetUserException extends ServiceException {
  public static final String UNABLE_TO_GET_USER = "Unable to get user: ";
}
