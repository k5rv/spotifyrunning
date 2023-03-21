package com.ksaraev.spotifyrun.config.playlist;

import com.ksaraev.spotifyrun.model.spotify.playlistdetails.SpotifyPlaylistItemDetails;
import com.ksaraev.spotifyrun.model.spotify.trackfeatures.SpotifyTrackItemFeatures;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlaylistConfig implements SpotifyRunPlaylistConfig {

  private SpotifyPlaylistItemDetails details;

  private SpotifyTrackItemFeatures musicFeatures;

  private Integer size;


}
