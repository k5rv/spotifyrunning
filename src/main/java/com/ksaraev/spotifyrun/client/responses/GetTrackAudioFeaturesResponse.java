package com.ksaraev.spotifyrun.client.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ksaraev.spotifyrun.client.items.SpotifyTrackItemAudioFeatures;

import java.util.List;

public record GetTrackAudioFeaturesResponse(
    @JsonProperty("audio_features") List<SpotifyTrackItemAudioFeatures> trackItemAudioFeatures) {}
