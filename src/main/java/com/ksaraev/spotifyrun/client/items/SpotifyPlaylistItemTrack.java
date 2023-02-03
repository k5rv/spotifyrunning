package com.ksaraev.spotifyrun.client.items;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record SpotifyPlaylistItemTrack(
    @JsonProperty("track") SpotifyTrackItem trackItem,
    @JsonProperty("added_at") String addedAt,
    @JsonProperty("added_by") SpotifyUserProfileItem addedBy,
    @JsonProperty("is_local") Boolean isLocal,
    @JsonProperty("primary_color") String primaryColor,
    @JsonProperty("video_thumbnail") Map<String, Object> videoThumbnail) {}
