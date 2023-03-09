package com.ksaraev.spotifyrun.exception.controller;

import lombok.experimental.StandardException;

@StandardException
public class RecommendationsNotFoundException extends RuntimeException {
  public static final String RECOMMENDATIONS_NOT_FOUND = "Recommendations not found: ";
}
