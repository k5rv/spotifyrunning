package com.ksaraev.spotifyrun.app.track;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Track implements AppTrack {
  private String id;
  private String name;
}
