package com.ksaraev.spotifyrun.client.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AddItemsResponse(@JsonProperty("snapshot_id") String snapshotId) {}
