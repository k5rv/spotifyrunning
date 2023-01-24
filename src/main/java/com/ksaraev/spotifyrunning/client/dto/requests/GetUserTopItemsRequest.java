package com.ksaraev.spotifyrunning.client.dto.requests;

import com.ksaraev.spotifyrunning.client.config.converters.SpotifyClientRequestParameter;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Value
@Builder
public class GetUserTopItemsRequest implements GetSpotifyUserItemsRequest {

  @Min(0)
  @Max(50)
  Integer limit;

  @Min(0)
  Integer offset;

  TimeRange timeRange;

  @Getter
  @AllArgsConstructor
  public enum TimeRange implements SpotifyClientRequestParameter {
    LONG_TERM("long_term"),
    MEDIUM_TERM("medium_term"),
    SHORT_TERM("short_term");

    private final String term;

    @Override
    public String toString() {
      return this.term;
    }

    @Override
    public String getParameter() {
      return this.term;
    }
  }
}
