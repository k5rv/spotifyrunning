package com.ksaraev.spotify.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record UpdatePlaylistItemsResponse(@JsonProperty("snapshot_id") @NotEmpty String snapshotId) {}
