package com.ksaraev.spotifyrunning.service;

import com.ksaraev.spotifyrunning.client.SpotifyClient;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyItem;
import com.ksaraev.spotifyrunning.client.dto.items.track.TrackItem;
import com.ksaraev.spotifyrunning.client.dto.items.userprofile.UserProfileItem;
import com.ksaraev.spotifyrunning.client.dto.requests.GetSpotifyUserItemsRequest;
import com.ksaraev.spotifyrunning.client.dto.requests.GetUserTopItemsRequest;
import com.ksaraev.spotifyrunning.client.dto.responses.SpotifyItemsResponse;
import com.ksaraev.spotifyrunning.config.requests.SpotifyRequestConfig;
import com.ksaraev.spotifyrunning.model.track.SpotifyTrack;
import com.ksaraev.spotifyrunning.model.track.TrackMapper;
import com.ksaraev.spotifyrunning.model.user.SpotifyUser;
import com.ksaraev.spotifyrunning.model.user.User;
import com.ksaraev.spotifyrunning.model.user.UserMapper;
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
  private final SpotifyClient spotifyClient;

  private final SpotifyRequestConfig spotifyRequestConfig;
  private final UserMapper userMapper;
  private final TrackMapper trackMapper;

  @Override
  public SpotifyUser getUser() {
    log.info("Getting current user profile");
    SpotifyItem spotifyItem = spotifyClient.getCurrentUserProfile();

    if (Objects.isNull(spotifyItem)) {
      throw new IllegalStateException("User profile is null");
    }

    UserProfileItem userProfileItem = (UserProfileItem) spotifyItem;

    User user = userMapper.toUser(userProfileItem);
    log.info("User received: {}", user);
    return user;
  }

  @Override
  public List<SpotifyTrack> getTopTracks() {
    log.info("Getting current user top tracks");

    GetSpotifyUserItemsRequest request =
        GetUserTopItemsRequest.builder()
            .limit(spotifyRequestConfig.getUserTopItemsRequestLimit())
            .timeRange(spotifyRequestConfig.getUserTopItemsRequestTimeRange())
            .build();

    SpotifyItemsResponse response = spotifyClient.getUserTopTracks(request);

    if (Objects.isNull(response)) {
      throw new IllegalStateException("User top tracks response is null");
    }

    List<SpotifyTrack> tracks =
        response.getItems().stream()
            .map(TrackItem.class::cast)
            .map(trackMapper::toTrack)
            .map(SpotifyTrack.class::cast)
            .toList();

    log.info("User top tracks received: {}", tracks);

    return tracks;
  }
}
