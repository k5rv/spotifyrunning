package com.ksaraev.spotifyrunning.client.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ksaraev.spotifyrunning.client.config.converters.SpotifyClientRequestParameter;

import java.util.List;

public record GetItemsRequest(@JsonProperty("ids") List<String> itemIds)
    implements SpotifyClientRequestParameter {
  @Override
  public String getParameter() {
    return String.join(",", this.itemIds);
  }
}
