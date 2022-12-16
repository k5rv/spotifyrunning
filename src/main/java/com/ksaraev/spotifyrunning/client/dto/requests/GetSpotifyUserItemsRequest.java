package com.ksaraev.spotifyrunning.client.dto.requests;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
  @JsonSubTypes.Type(value = GetRecommendationItemsRequest.class),
  @JsonSubTypes.Type(value = GetUserTopItemsRequest.class)
})
public interface GetSpotifyUserItemsRequest {

  Integer getLimit();

  Integer getOffset();
}
