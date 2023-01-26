package com.ksaraev.spotifyrunning.client.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ksaraev.spotifyrunning.client.items.SpotifyTrackItem;

import java.net.URL;
import java.util.List;

public record GetUserTopTracksResponse(
    URL href,
    @JsonProperty("items") List<SpotifyTrackItem> spotifyTrackItems,
    Integer limit,
    Integer offset,
    Integer total,
    String next,
    String previous) {}
