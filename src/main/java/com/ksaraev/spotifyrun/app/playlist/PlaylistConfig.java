package com.ksaraev.spotifyrun.app.playlist;

import com.ksaraev.spotifyrun.spotify.model.playlistdetails.SpotifyPlaylistItemDetails;
import com.ksaraev.spotifyrun.spotify.model.trackfeatures.SpotifyTrackItemFeatures;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlaylistConfig implements AppPlaylistConfig {

  private SpotifyPlaylistItemDetails details;

  private SpotifyTrackItemFeatures musicFeatures;

  private Integer size;
}
