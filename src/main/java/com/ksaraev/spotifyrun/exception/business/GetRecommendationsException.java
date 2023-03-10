package com.ksaraev.spotifyrun.exception.business;

import lombok.experimental.StandardException;

@StandardException
public class GetRecommendationsException extends RuntimeException {
  public static final String UNABLE_TO_GET_RECOMMENDATIONS = "Unable to get recommendations: ";
}
