package com.ksaraev.suddenrun.track;

import com.google.common.base.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuddenrunTrack implements AppTrack {
  private String id;
  private String name;
}
