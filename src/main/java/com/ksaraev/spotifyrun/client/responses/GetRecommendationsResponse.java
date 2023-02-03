package com.ksaraev.spotifyrun.client.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ksaraev.spotifyrun.client.items.SpotifyTrackItem;

import java.util.List;
import java.util.Map;

public record GetRecommendationsResponse(
    @JsonProperty("tracks") List<SpotifyTrackItem> trackItems, List<Map<String, Object>> seeds) {}
