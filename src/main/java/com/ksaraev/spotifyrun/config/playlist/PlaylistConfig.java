package com.ksaraev.spotifyrun.config.playlist;

import com.ksaraev.spotifyrun.model.playlistdetails.SpotifyPlaylistDetails;
import com.ksaraev.spotifyrun.model.trackfeatures.SpotifyTrackFeatures;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlaylistConfig implements SpotifyRunPlaylistConfig {

  private SpotifyPlaylistDetails details;

  private SpotifyTrackFeatures musicFeatures;

  private Integer size;


}
