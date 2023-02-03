package com.ksaraev.spotifyrun.client.items;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

public record SpotifyAlbumItem(
    @JsonProperty("album_type") String albumType,
    @JsonProperty("total_tracks") Integer totalTracks,
    @JsonProperty("release_date") String releaseDate,
    @JsonProperty("release_date_precision") String releaseDatePrecision,
    @JsonProperty("available_markets") List<String> availableMarkets,
    @JsonProperty("artists") List<SpotifyArtistItem> artistItems,
    @JsonProperty("external_urls") Map<String, Object> externalUrls,
    String id,
    String name,
    List<Map<String, Object>> restrictions,
    String type,
    URI uri,
    URL href,
    List<Map<String, Object>> images) {}
