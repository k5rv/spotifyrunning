package com.suddenrun.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record RemovePlaylistItemsResponse(
    @JsonProperty("snapshot_id") @NotEmpty String snapshotId) {}
