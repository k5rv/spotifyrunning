package com.ksaraev.spotifyrun.client.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ksaraev.spotifyrun.client.items.SpotifyTrackItem;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

public record GetRecommendationsResponse(
    @JsonProperty("tracks") @Valid List<SpotifyTrackItem> trackItems,
    List<Map<String, Object>> seeds) {}
