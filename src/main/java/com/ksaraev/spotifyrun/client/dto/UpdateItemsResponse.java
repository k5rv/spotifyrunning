package com.ksaraev.spotifyrun.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record UpdateItemsResponse(@JsonProperty("snapshot_id") @NotEmpty String snapshotId) {}
