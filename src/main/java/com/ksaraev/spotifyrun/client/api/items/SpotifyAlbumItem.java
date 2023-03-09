package com.ksaraev.spotifyrun.client.api.items;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
import lombok.Builder;

@Builder
public record SpotifyAlbumItem(
    @JsonProperty("album_type") String albumType,
    @JsonProperty("album_group") String albumGroup,
    @JsonProperty("total_tracks") Integer totalTracks,
    @JsonProperty("release_date") String releaseDate,
    @JsonProperty("release_date_precision") String releaseDatePrecision,
    @JsonProperty("available_markets") List<String> availableMarkets,
    @JsonProperty("artists") List<SpotifyArtistItem> artistItems,
    @JsonProperty("external_urls") Map<String, Object> externalUrls,
    @NotNull String id,
    @NotEmpty String name,
    List<Map<String, Object>> restrictions,
    String type,
    @NotNull URI uri,
    URL href,
    List<Map<String, Object>> images) {}


