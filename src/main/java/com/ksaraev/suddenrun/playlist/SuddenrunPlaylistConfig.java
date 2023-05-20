package com.ksaraev.suddenrun.playlist;

import com.ksaraev.spotify.model.playlistdetails.SpotifyPlaylistItemDetails;
import com.ksaraev.spotify.model.trackfeatures.SpotifyTrackItemFeatures;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SuddenrunPlaylistConfig implements AppPlaylistConfig {

  private SpotifyPlaylistItemDetails details;

  private SpotifyTrackItemFeatures musicFeatures;

  private Integer size;
}
