package com.ksaraev.spotifyrun.client.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ksaraev.spotifyrun.client.items.SpotifyTrackItem;
import jakarta.validation.Valid;

import java.net.URL;
import java.util.List;

public record GetUserTopTracksResponse(
    URL href,
    @JsonProperty("items") @Valid List<SpotifyTrackItem> trackItems,
    Integer limit,
    Integer offset,
    Integer total,
    String next,
    String previous) {}
