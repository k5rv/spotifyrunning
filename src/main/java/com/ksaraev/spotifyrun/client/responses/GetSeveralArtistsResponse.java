package com.ksaraev.spotifyrun.client.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ksaraev.spotifyrun.client.items.SpotifyArtistItem;

import java.util.List;

public record GetSeveralArtistsResponse(
    @JsonProperty("artists") List<SpotifyArtistItem> artistItems) {}
