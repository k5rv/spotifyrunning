package com.ksaraev.spotifyrun.exception.spotify;

import com.ksaraev.spotifyrun.exception.service.ServiceException;
import lombok.experimental.StandardException;

@StandardException
public class TooManyRequestsException extends ServiceException {
  public static final String TOO_MANY_REQUESTS = "Too many requests: ";
}
