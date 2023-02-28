package com.ksaraev.spotifyrun.client.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ksaraev.spotifyrun.client.feign.converters.SpotifyClientRequestParameter;

import java.util.List;

public record GetItemsRequest(@JsonProperty("ids") List<String> itemIds)
    implements SpotifyClientRequestParameter {
  @Override
  public String getParameter() {
    return String.join(",", this.itemIds);
  }
}
