package com.ksaraev.spotifyrunning.client.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ksaraev.spotifyrunning.client.items.SpotifyTrackItemAudioFeatures;

import java.util.List;

public record GetTrackAudioFeaturesResponse(
    @JsonProperty("audio_features")
        List<SpotifyTrackItemAudioFeatures> spotifyTrackItemAudioFeatures) {}
