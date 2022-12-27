package com.ksaraev.spotifyrunning.service;

import com.ksaraev.spotifyrunning.client.SpotifyClient;
import com.ksaraev.spotifyrunning.client.dto.items.artist.ArtistItem;
import com.ksaraev.spotifyrunning.client.dto.requests.GetItemsRequest;
import com.ksaraev.spotifyrunning.client.dto.requests.GetSpotifyItemsRequest;
import com.ksaraev.spotifyrunning.client.dto.responses.SpotifyItemsResponse;
import com.ksaraev.spotifyrunning.model.artist.ArtistMapper;
import com.ksaraev.spotifyrunning.model.artist.SpotifyArtist;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@Validated
@AllArgsConstructor
public class ArtistService implements SpotifyArtistService {
  private final SpotifyClient spotifyClient;
  private final ArtistMapper artistMapper;

  @Override
  public List<SpotifyArtist> getArtists(@NotNull List<String> ids) {
    log.info("Getting artists with ids: {}", ids);
    GetSpotifyItemsRequest request = GetItemsRequest.builder().ids(ids).build();

    SpotifyItemsResponse response = spotifyClient.getArtists(request);

    if (Objects.isNull(response)) {
      throw new IllegalStateException("Artists response is null");
    }

    List<SpotifyArtist> artists =
        response.getItems().stream()
            .map(ArtistItem.class::cast)
            .map(artistMapper::toArtist)
            .map(SpotifyArtist.class::cast)
            .toList();

    log.info("Artists received: {}", artists);
    return artists;
  }
}
