package com.suddenrun.app.playlist;

import com.suddenrun.spotify.model.playlistdetails.SpotifyPlaylistItemDetails;
import com.suddenrun.spotify.model.trackfeatures.SpotifyTrackItemFeatures;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlaylistConfig implements AppPlaylistConfig {

  private SpotifyPlaylistItemDetails details;

  private SpotifyTrackItemFeatures musicFeatures;

  private Integer size;
}
