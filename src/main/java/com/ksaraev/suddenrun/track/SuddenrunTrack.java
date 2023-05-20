package com.ksaraev.suddenrun.track;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SuddenrunTrack implements AppTrack {
  private String id;
  private String name;
}
