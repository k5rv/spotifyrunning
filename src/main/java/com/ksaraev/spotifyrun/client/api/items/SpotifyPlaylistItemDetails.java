package com.ksaraev.spotifyrun.client.api.items;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record SpotifyPlaylistItemDetails(
    @JsonProperty("collaborative") Boolean isCollaborative,
    @JsonProperty("public") Boolean isPublic,
    @NotEmpty String name,
    String description) {}
