package com.ksaraev.spotifyrun.client.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;

public record AddItemsResponse(@JsonProperty("snapshot_id") @NotEmpty String snapshotId) {}
