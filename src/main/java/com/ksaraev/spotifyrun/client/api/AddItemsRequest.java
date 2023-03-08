package com.ksaraev.spotifyrun.client.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import lombok.Builder;

@Builder
public record AddItemsRequest(@JsonProperty("uris") @NotEmpty List<@NotNull URI> itemUris) {}
