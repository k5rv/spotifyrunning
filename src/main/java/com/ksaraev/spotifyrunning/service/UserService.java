package com.ksaraev.spotifyrunning.service;

import com.ksaraev.spotifyrunning.client.SpotifyClient;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyItem;
import com.ksaraev.spotifyrunning.client.dto.items.track.TrackItem;
import com.ksaraev.spotifyrunning.client.dto.items.userprofile.UserProfileItem;
import com.ksaraev.spotifyrunning.client.dto.requests.GetSpotifyUserItemsRequest;
import com.ksaraev.spotifyrunning.client.dto.requests.GetUserTopItemsRequest;
import com.ksaraev.spotifyrunning.client.dto.responses.SpotifyItemsResponse;
import com.ksaraev.spotifyrunning.config.topitems.SpotifyUserTopItemsConfig;
import com.ksaraev.spotifyrunning.model.track.SpotifyTrack;
import com.ksaraev.spotifyrunning.model.track.TrackMapper;
import com.ksaraev.spotifyrunning.model.user.SpotifyUser;
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

  private final UserMapper userMapper;
  private final TrackMapper trackMapper;

  private final SpotifyClient spotifyClient;

  private final SpotifyUserTopItemsConfig spotifyUserTopItemsConfig;

  @Override
  public SpotifyUser getUser() {
    SpotifyItem spotifyItem = spotifyClient.getCurrentUserProfile();
    UserProfileItem userProfileItem = (UserProfileItem) spotifyItem;
    return userMapper.toUser(userProfileItem);
  }

  @Override
  public List<SpotifyTrack> getTopTracks() {
    GetSpotifyUserItemsRequest request =
        GetUserTopItemsRequest.builder()
            .limit(spotifyUserTopItemsConfig.getUserTopItemsRequestLimit())
            .timeRange(
                GetUserTopItemsRequest.TimeRange.valueOf(
                    spotifyUserTopItemsConfig.getUserTopItemsRequestTimeRange().toUpperCase()))
            .build();

    SpotifyItemsResponse response = spotifyClient.getUserTopTracks(request);

    List<SpotifyTrack> tracks =
        response.getItems().stream()
            .filter(Objects::nonNull)
            .map(TrackItem.class::cast)
            .map(trackMapper::toTrack)
            .map(SpotifyTrack.class::cast)
            .toList();

    if (tracks.isEmpty()) {
      return List.of();
    }

    return tracks;
  }
}
