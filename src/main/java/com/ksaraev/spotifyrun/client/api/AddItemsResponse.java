package com.ksaraev.spotifyrun.client.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record AddItemsResponse(@JsonProperty("snapshot_id") @NotEmpty String snapshotId) {}
