package com.ksaraev.spotifyrunning.service;

import com.ksaraev.spotifyrunning.client.SpotifyClient;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyItem;
import com.ksaraev.spotifyrunning.client.dto.items.artist.ArtistItem;
import com.ksaraev.spotifyrunning.client.dto.requests.GetItemsRequest;
import com.ksaraev.spotifyrunning.client.dto.requests.GetSpotifyItemsRequest;
import com.ksaraev.spotifyrunning.client.dto.responses.SpotifyItemsResponse;
import com.ksaraev.spotifyrunning.model.artist.ArtistMapper;
import com.ksaraev.spotifyrunning.model.artist.SpotifyArtist;
import com.ksaraev.spotifyrunning.model.track.SpotifyTrack;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
@AllArgsConstructor
public class ArtistService {
  private final SpotifyClient spotifyClient;
  private final ArtistMapper artistMapper;

  public List<SpotifyArtist> getArtists(@NotEmpty List<SpotifyTrack> tracks) {

    List<String> ids =
        tracks.stream()
            .map(SpotifyTrack::getArtists)
            .flatMap(List::stream)
            .map(SpotifyArtist::getId)
            .collect(Collectors.toList());

    return getSeveralArtists(ids);
  }

  public List<SpotifyArtist> getSeveralArtists(@NotEmpty List<String> ids) {

    GetSpotifyItemsRequest request = GetItemsRequest.builder().ids(ids).build();

    SpotifyItemsResponse response = spotifyClient.getArtists(request);

    if (response == null) {
      throw new RuntimeException("Spotify artists response is null");
    }

    List<SpotifyItem> spotifyItems = response.getItems();

    if (spotifyItems == null) {
      throw new RuntimeException("Spotify artist list is null");
    }

    List<SpotifyArtist> spotifyArtists =
        spotifyItems.stream()
            .map(ArtistItem.class::cast)
            .map(artistMapper::toArtist)
            .collect(Collectors.toList());
    log.info("Spotify artists received: {}", spotifyArtists);
    return spotifyArtists;
  }
}
