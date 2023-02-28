package com.ksaraev.spotifyrun.client.items;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;

public record SpotifyPlaylistItemDetails(
    @JsonProperty("collaborative") Boolean isCollaborative,
    @JsonProperty("public") Boolean isPublic,
    @NotEmpty String name,
    String description) {}
