package com.ksaraev.spotifyrun.exception.business;

import lombok.experimental.StandardException;

@StandardException
public class AddMusicRecommendationsException extends RuntimeException {
  public static final String UNABLE_TO_ADD_MUSIC_RECOMMENDATIONS = "Unable to add music recommendations: ";
}
