package com.ksaraev.spotifyrun.client.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ksaraev.spotifyrun.client.api.items.SpotifyTrackDto;
import jakarta.validation.Valid;
import java.net.URL;
import java.util.List;
import lombok.Builder;

@Builder
public record GetUserTopTracksResponse(
    URL href,
    @JsonProperty("items") @Valid List<SpotifyTrackDto> trackItems,
    Integer limit,
    Integer offset,
    Integer total,
    String next,
    String previous) {}
