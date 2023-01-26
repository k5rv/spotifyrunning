package com.ksaraev.spotifyrunning.client.items;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifyPlaylistItemDetails(
    @JsonProperty("collaborative") Boolean isCollaborative,
    @JsonProperty("public") Boolean isPublic,
    String name,
    String description) {}
