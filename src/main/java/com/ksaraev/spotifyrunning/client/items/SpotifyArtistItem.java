package com.ksaraev.spotifyrunning.client.items;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

public record SpotifyArtistItem(
    @JsonProperty("followers") Map<String, Object> followers,
    @JsonProperty("external_urls") Map<String, Object> externalUrls,
    String id,
    String name,
    Integer popularity,
    List<String> genres,
    String type,
    URI uri,
    URL href,
    List<Map<String, Object>> images) {}
