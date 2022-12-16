package com.ksaraev.spotifyrunning.client.dto.requests;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class GetItemsRequest implements GetSpotifyItemsRequest {
  List<String> ids;

  @Override
  public String getParameter() {
    return String.join(",", this.ids);
  }
}
