package com.ksaraev.spotifyrun.client.items;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

@Builder
public record SpotifyArtistItem(
    @JsonProperty("followers") Map<String, Object> followers,
    @JsonProperty("external_urls") Map<String, Object> externalUrls,
    @NotNull String id,
    @NotEmpty String name,
    Integer popularity,
    List<String> genres,
    String type,
    @NotNull URI uri,
    URL href,
    List<Map<String, Object>> images) {}
