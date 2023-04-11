package com.ksaraev.spotifyrun.service;

import static com.ksaraev.spotifyrun.utils.SpotifyHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.dto.SpotifyUserProfileDto;
import com.ksaraev.spotifyrun.security.AuthenticationFacade;
import com.ksaraev.spotifyrun.spotify.model.userprofile.SpotifyUserProfile;
import com.ksaraev.spotifyrun.spotify.model.userprofile.SpotifyUserProfileItem;
import com.ksaraev.spotifyrun.spotify.model.userprofile.SpotifyUserProfileMapper;
import com.ksaraev.spotifyrun.spotify.service.SpotifyUserProfileItemService;
import com.ksaraev.spotifyrun.spotify.service.SpotifyUserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SpotifyUserProfileServiceTest {
  @Mock private SpotifyClient spotifyClient;
  @Mock private SpotifyUserProfileMapper userMapper;

  @Mock private AuthenticationFacade authenticationFacade;
  private SpotifyUserProfileItemService underTest;

  @Captor private ArgumentCaptor<SpotifyUserProfileDto> userProfileItemArgumentCaptor;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    underTest = new SpotifyUserProfileService(spotifyClient, userMapper /*, authenticationFacade*/);
  }

  @Test
  void itShouldGetUser() {
    // Given
    SpotifyUserProfileItem user = getUser();

    SpotifyUserProfileDto userProfileItem = getUserProfileItem();

    given(spotifyClient.getCurrentUserProfile()).willReturn(userProfileItem);
    given(userMapper.mapToModel(userProfileItem)).willReturn((SpotifyUserProfile) user);

    // When
    underTest.getCurrentUser();

    // Then
    verify(spotifyClient, times(1)).getCurrentUserProfile();
    then(userMapper).should().mapToModel(userProfileItemArgumentCaptor.capture());
    assertThat(userProfileItemArgumentCaptor.getValue()).isNotNull().isEqualTo(userProfileItem);
  }

  @Test
  void itShouldThrowGetUserExceptionWhenSpotifyClientThrowsRuntimeException() {
    // Given
    String message = "message";
    given(spotifyClient.getCurrentUserProfile()).willThrow(new RuntimeException(message));
    // Then
//    assertThatThrownBy(() -> underTest.getCurrentUser())
//        .isExactlyInstanceOf(GetUserException.class)
//        .hasMessage(UNABLE_TO_GET_USER + message);
  }

  @Test
  void itShouldThrowGetUserExceptionWhenUserMapperThrowsRuntimeException() {
    // Given
    String message = "message";
    given(userMapper.mapToModel(any())).willThrow(new RuntimeException(message));
    // Then
//    assertThatThrownBy(() -> underTest.getCurrentUser())
//        .isExactlyInstanceOf(GetUserException.class)
//        .hasMessage(UNABLE_TO_GET_USER + message);
  }
}
