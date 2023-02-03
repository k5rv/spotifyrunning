package com.ksaraev.spotifyrun.client.items;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

public record SpotifyPlaylistItem(
    @JsonProperty("owner") SpotifyUserProfileItem userProfileItem,
    @JsonProperty("tracks") SpotifyPlaylistItemMusic playlistItemMusic,
    @JsonProperty("public") Boolean isPublic,
    @JsonProperty("snapshot_id") String snapshotId,
    @JsonProperty("collaborative") Boolean isCollaborative,
    @JsonProperty("followers") Map<String, Object> followers,
    @JsonProperty("external_urls") Map<String, Object> externalUrls,
    String id,
    String name,
    String description,
    String type,
    URI uri,
    URL href,
    List<Map<String, Object>> images) {}
