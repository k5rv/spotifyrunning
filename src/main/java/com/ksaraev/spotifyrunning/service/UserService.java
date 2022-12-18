package com.ksaraev.spotifyrunning.service;

import com.ksaraev.spotifyrunning.client.SpotifyClient;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyItem;
import com.ksaraev.spotifyrunning.client.dto.items.track.TrackItem;
import com.ksaraev.spotifyrunning.client.dto.items.userprofile.UserProfileItem;
import com.ksaraev.spotifyrunning.client.dto.requests.GetSpotifyUserItemsRequest;
import com.ksaraev.spotifyrunning.client.dto.responses.SpotifyItemsResponse;
import com.ksaraev.spotifyrunning.model.track.TrackMapper;
import com.ksaraev.spotifyrunning.model.track.SpotifyTrack;
import com.ksaraev.spotifyrunning.model.user.SpotifyUser;
import com.ksaraev.spotifyrunning.model.user.User;
import com.ksaraev.spotifyrunning.model.user.UserMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@Validated
@AllArgsConstructor
public class UserService {
  private final SpotifyClient spotifyClient;
  private final UserMapper userMapper;
  private final TrackMapper trackMapper;

  public SpotifyUser getUser() {

    SpotifyItem spotifyItem = spotifyClient.getCurrentUserProfile();

    if (spotifyItem == null) {
      throw new NullPointerException("Spotify user profile is null");
    }

    UserProfileItem userProfileItem = (UserProfileItem) spotifyItem;

    User user = userMapper.toUser(userProfileItem);
    log.info("User received: {}", user);
    return user;
  }

  public List<SpotifyTrack> getTopTracks(@Valid @NotNull GetSpotifyUserItemsRequest request) {

    SpotifyItemsResponse response = spotifyClient.getUserTopTracks(request);

    if (response == null) {
      log.error("Spotify user top tracks response is null, request: {}", request);
      return Collections.emptyList();
    }

    List<SpotifyTrack> spotifyTracks =
        response.getItems().stream()
            .map(TrackItem.class::cast)
            .map(trackMapper::toTrack)
            .map(SpotifyTrack.class::cast)
            .toList();

    log.info("Top user tracks received: {}", spotifyTracks);
    return spotifyTracks;
  }
}
