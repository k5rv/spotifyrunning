package com.ksaraev.spotifyrunning.client.dto.requests;

import com.ksaraev.spotifyrunning.client.config.converters.SpotifyClientRequestParameter;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface GetSpotifyItemsRequest extends SpotifyClientRequestParameter {
  @NotNull
  List<String> getIds();
}
