package com.ksaraev.spotifyrunning.client.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ksaraev.spotifyrunning.client.items.SpotifyTrackItem;

import java.util.List;
import java.util.Map;

public record GetRecommendationsResponse(
    @JsonProperty("tracks") List<SpotifyTrackItem> spotifyTrackItems,
    List<Map<String, Object>> seeds) {}
