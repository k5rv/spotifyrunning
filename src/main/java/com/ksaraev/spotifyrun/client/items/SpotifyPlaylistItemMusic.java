package com.ksaraev.spotifyrun.client.items;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URL;
import java.util.List;

public record SpotifyPlaylistItemMusic(
    @JsonProperty("items") List<SpotifyPlaylistItemTrack> playlistItemTracks,
    URL href,
    Integer limit,
    Integer offset,
    Integer total,
    String next,
    String previous) {}
