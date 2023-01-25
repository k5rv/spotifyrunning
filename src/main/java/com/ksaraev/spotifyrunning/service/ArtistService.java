package com.ksaraev.spotifyrunning.service;

import com.ksaraev.spotifyrunning.client.SpotifyClient;
import com.ksaraev.spotifyrunning.client.dto.items.artist.SpotifyArtistDTO;
import com.ksaraev.spotifyrunning.client.dto.requests.GetItemsRequest;
import com.ksaraev.spotifyrunning.client.dto.requests.GetSpotifyItemsRequest;
import com.ksaraev.spotifyrunning.client.dto.responses.SpotifyItemsResponse;
import com.ksaraev.spotifyrunning.model.artist.ArtistMapper;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyArtist;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

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
    GetSpotifyItemsRequest request = GetItemsRequest.builder().ids(ids).build();
    SpotifyItemsResponse response = spotifyClient.getArtists(request);

    List<SpotifyArtist> artists =
        response.getItems().stream()
            .filter(Objects::nonNull)
            .map(SpotifyArtistDTO.class::cast)
            .map(artistMapper::toArtist)
            .map(SpotifyArtist.class::cast)
            .toList();

    if (artists.isEmpty()) {
      return List.of();
    }

    return artists;
  }
}
