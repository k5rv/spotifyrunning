package com.ksaraev.spotifyrun.client.items;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import java.net.URL;
import java.util.List;
import lombok.Builder;

@Builder
public record SpotifyPlaylistItemMusic(
    @JsonProperty("items") @Valid List<SpotifyPlaylistItemTrack> playlistItemTracks,
    URL href,
    Integer limit,
    Integer offset,
    Integer total,
    String next,
    String previous) {}
