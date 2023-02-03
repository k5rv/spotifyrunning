package com.ksaraev.spotifyrun.service;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.items.SpotifyUserProfileItem;
import com.ksaraev.spotifyrun.client.requests.GetUserTopTracksRequest;
import com.ksaraev.spotifyrun.client.responses.GetUserTopTracksResponse;
import com.ksaraev.spotifyrun.config.requests.SpotifyGetUserTopTracksRequestConfig;
import com.ksaraev.spotifyrun.model.spotify.SpotifyTrack;
import com.ksaraev.spotifyrun.model.spotify.SpotifyUser;
import com.ksaraev.spotifyrun.model.track.TrackMapper;
import com.ksaraev.spotifyrun.model.user.UserMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@Validated
@AllArgsConstructor
public class UserService implements SpotifyUserService {

  private final UserMapper userMapper;
  private final TrackMapper trackMapper;

  private final SpotifyClient spotifyClient;

  private final SpotifyGetUserTopTracksRequestConfig requestConfig;

  @Override
  public SpotifyUser getUser() {
    SpotifyUserProfileItem userProfileResponse = spotifyClient.getCurrentUserProfile();
    return userMapper.toModel(userProfileResponse);
  }

  @Override
  public List<SpotifyTrack> getTopTracks() {
    GetUserTopTracksRequest request =
        GetUserTopTracksRequest.builder()
            .timeRange(GetUserTopTracksRequest.TimeRange.valueOf(requestConfig.getTimeRange()))
            .limit(requestConfig.getLimit())
            .build();

    GetUserTopTracksResponse response = spotifyClient.getUserTopTracks(request);

    List<SpotifyTrack> tracks =
        response.trackItems().stream()
            .filter(Objects::nonNull)
            .map(trackMapper::toModel)
            .map(SpotifyTrack.class::cast)
            .toList();

    if (tracks.isEmpty()) {
      return List.of();
    }

    return tracks;
  }
}
