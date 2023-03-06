package com.ksaraev.spotifyrun.client.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ksaraev.spotifyrun.client.items.SpotifyTrackItem;
import jakarta.validation.Valid;
import java.net.URL;
import java.util.List;
import lombok.Builder;

@Builder
public record GetUserTopTracksResponse(
    URL href,
    @JsonProperty("items") @Valid List<SpotifyTrackItem> trackItems,
    Integer limit,
    Integer offset,
    Integer total,
    String next,
    String previous) {}
