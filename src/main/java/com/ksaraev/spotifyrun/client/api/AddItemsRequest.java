package com.ksaraev.spotifyrun.client.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.net.URI;
import java.util.List;
import lombok.Builder;

@Builder
public record AddItemsRequest(
    @JsonProperty("uris") @Size(min = 1, max = 100) List<@NotNull URI> itemUris) {}
