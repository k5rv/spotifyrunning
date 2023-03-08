package com.ksaraev.spotifyrun.client.items;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import lombok.Builder;

@Builder
public record SpotifyPlaylistItemTrack(
    @JsonProperty("track") @Valid @NotNull SpotifyTrackItem trackItem,
    @JsonProperty("added_at") @NotNull String addedAt,
    @JsonProperty("added_by") @Valid @NotNull SpotifyUserProfileItem addedBy,
    @JsonProperty("is_local") Boolean isLocal,
    @JsonProperty("primary_color") String primaryColor,
    @JsonProperty("video_thumbnail") Map<String, Object> videoThumbnail) {}
