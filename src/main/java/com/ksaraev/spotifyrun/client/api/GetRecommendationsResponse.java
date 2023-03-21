package com.ksaraev.spotifyrun.client.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ksaraev.spotifyrun.client.api.items.SpotifyTrackDto;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.Builder;

@Builder
public record GetRecommendationsResponse(
    @JsonProperty("tracks") @Valid List<SpotifyTrackDto> trackItems,
    List<Map<String, Object>> seeds) {}
