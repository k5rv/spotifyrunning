package com.ksaraev.spotifyrun.client.items;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

public record SpotifyTrackItem(
    @JsonProperty("album") SpotifyAlbumItem albumItem,
    @JsonProperty("preview_url") URL previewUrl,
    @JsonProperty("is_local") Boolean isLocal,
    @JsonProperty("is_playable") Boolean isPlayable,
    @JsonProperty("duration_ms") Integer durationMs,
    @JsonProperty("track_number") Integer trackNumber,
    @JsonProperty("disc_number") Integer discNumber,
    @JsonProperty("linked_from") SpotifyAlbumItem sourceAlbumItem,
    @JsonProperty("artists") List<SpotifyArtistItem> artistItems,
    @JsonProperty("available_markets") List<String> availableMarkets,
    @JsonProperty("external_ids") Map<String, Object> externalIds,
    @JsonProperty("external_urls") Map<String, Object> externalUrls,
    String id,
    String name,
    Integer popularity,
    String type,
    URI uri,
    URL href,
    Boolean explicit) {}
