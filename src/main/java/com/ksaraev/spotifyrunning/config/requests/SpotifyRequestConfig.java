package com.ksaraev.spotifyrunning.config.requests;

import static com.ksaraev.spotifyrunning.client.dto.requests.GetUserTopItemsRequest.TimeRange;

public interface SpotifyRequestConfig {

  Integer getUserTopItemsRequestLimit();

  Integer getRecommendationItemsRequestLimit();

  TimeRange getUserTopItemsRequestTimeRange();
}
