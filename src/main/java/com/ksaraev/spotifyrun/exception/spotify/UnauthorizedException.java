package com.ksaraev.spotifyrun.exception.spotify;

import com.ksaraev.spotifyrun.exception.service.ServiceException;
import lombok.experimental.StandardException;

@StandardException
public class UnauthorizedException extends ServiceException {
  public static final String UNAUTHORIZED = "Unauthorized: ";
}
