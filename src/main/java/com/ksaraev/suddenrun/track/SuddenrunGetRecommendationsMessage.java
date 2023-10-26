package com.ksaraev.suddenrun.track;

import com.ksaraev.spotify.model.track.SpotifyTrackItem;
import com.ksaraev.spotify.model.trackfeatures.SpotifyTrackItemFeatures;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
public class SuddenrunGetRecommendationsMessage {
  private String playlistId;
  private List<SpotifyTrackItem> trackItems;
  private SpotifyTrackItemFeatures features;
}
