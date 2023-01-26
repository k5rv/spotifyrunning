package com.ksaraev.spotifyrunning.client.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ksaraev.spotifyrunning.client.items.SpotifyArtistItem;

import java.util.List;

public record GetSeveralArtistsResponse(
    @JsonProperty("artists") List<SpotifyArtistItem> spotifyArtistItems) {}
