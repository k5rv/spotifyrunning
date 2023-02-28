package com.ksaraev.spotifyrun.client.items;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

public record SpotifyPlaylistItem(
    @JsonProperty("owner") @NotNull SpotifyUserProfileItem userProfileItem,
    @JsonProperty("tracks") SpotifyPlaylistItemMusic playlistItemMusic,
    @JsonProperty("public") Boolean isPublic,
    @JsonProperty("snapshot_id") @NotNull String snapshotId,
    @JsonProperty("collaborative") Boolean isCollaborative,
    @JsonProperty("primary_color") String primaryColor,
    @JsonProperty("followers") Map<String, Object> followers,
    @JsonProperty("external_urls") Map<String, Object> externalUrls,
    @NotNull String id,
    @NotEmpty String name,
    String description,
    String type,
    @NotNull URI uri,
    URL href,
    List<Map<String, Object>> images) {}
