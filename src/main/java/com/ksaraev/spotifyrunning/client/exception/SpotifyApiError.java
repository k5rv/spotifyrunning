package com.ksaraev.spotifyrunning.client.exception;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

@Data
@JsonRootName(value = "error")
public class SpotifyApiError {
  private Integer status;
  private String message;
}
