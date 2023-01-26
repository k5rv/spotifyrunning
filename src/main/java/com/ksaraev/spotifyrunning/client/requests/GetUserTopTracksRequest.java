package com.ksaraev.spotifyrunning.client.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public record GetUserTopTracksRequest(Integer limit, Integer offset, TimeRange timeRange) {

  @Builder
  public GetUserTopTracksRequest {}

  @Getter
  @AllArgsConstructor
  public enum TimeRange {
    LONG_TERM("long_term"),
    MEDIUM_TERM("medium_term"),
    SHORT_TERM("short_term");

    private final String term;

    @Override
    public String toString() {
      return this.term;
    }
  }
}
