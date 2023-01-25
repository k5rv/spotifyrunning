package com.ksaraev.spotifyrunning.client.dto.items.audiofeatures;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyAudioFeatures;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyItem;

import java.net.URL;

@JsonDeserialize(as = SpotifyTrackAudioFeaturesDTO.class)
public interface SpotifyTrackAudioFeaturesItem extends SpotifyItem, SpotifyAudioFeatures {

  URL getTrackHref();

  URL getAnalysisUrl();
}
