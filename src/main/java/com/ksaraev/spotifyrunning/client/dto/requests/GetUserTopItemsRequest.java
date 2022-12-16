package com.ksaraev.spotifyrunning.client.dto.requests;

import com.ksaraev.spotifyrunning.client.config.converters.SpotifyClientRequestParameter;
import feign.Param;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Value
@Builder
public class GetUserTopItemsRequest implements GetSpotifyUserItemsRequest {

  @Min(0)
  @Max(50)
  Integer limit;

  @Min(0)
  Integer offset;

  @Param("time_range")
  TimeRange timeRange;

  @Getter
  @AllArgsConstructor
  public enum TimeRange implements SpotifyClientRequestParameter {
    LONG_TERM("long_term"),
    MEDIUM_TERM("medium_term"),
    SHORT_TERM("short_term");

    private final String timeRange;

    @Override
    public String toString() {
      return this.timeRange;
    }

    @Override
    public String getParameter() {
      return this.timeRange;
    }
  }
}
