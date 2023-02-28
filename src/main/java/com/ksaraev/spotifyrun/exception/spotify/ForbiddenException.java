package com.ksaraev.spotifyrun.exception.spotify;

import com.ksaraev.spotifyrun.exception.service.ServiceException;
import lombok.experimental.StandardException;

@StandardException
public class ForbiddenException extends ServiceException {
  public static final String FORBIDDEN = "Forbidden: ";
}
