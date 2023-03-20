package com.ksaraev.spotifyrun.service;

import static com.ksaraev.spotifyrun.exception.business.GetUserException.UNABLE_TO_GET_USER;
import static com.ksaraev.spotifyrun.utils.SpotifyHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.api.items.SpotifyUserProfileItem;
import com.ksaraev.spotifyrun.exception.business.GetUserException;
import com.ksaraev.spotifyrun.model.user.SpotifyUser;
import com.ksaraev.spotifyrun.model.user.AppUser;
import com.ksaraev.spotifyrun.model.user.AppUserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class AppUserServiceTest {
  @Mock private SpotifyClient spotifyClient;
  @Mock private AppUserMapper appUserMapper;
  private SpotifyUserService underTest;

  @Captor private ArgumentCaptor<SpotifyUserProfileItem> userProfileItemArgumentCaptor;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    underTest = new UserService(spotifyClient, appUserMapper);
  }

  @Test
  void itShouldGetUser() {
    // Given
    SpotifyUser user = getUser();

    SpotifyUserProfileItem userProfileItem = getUserProfileItem();

    given(spotifyClient.getCurrentUserProfile()).willReturn(userProfileItem);
    given(appUserMapper.mapToUser(userProfileItem)).willReturn((AppUser) user);

    // When
    underTest.getCurrentUser();

    // Then
    verify(spotifyClient, times(1)).getCurrentUserProfile();
    then(appUserMapper).should().mapToUser(userProfileItemArgumentCaptor.capture());
    assertThat(userProfileItemArgumentCaptor.getValue()).isNotNull().isEqualTo(userProfileItem);
  }

  @Test
  void itShouldThrowGetUserExceptionWhenSpotifyClientThrowsRuntimeException() {
    // Given
    String message = "message";
    given(spotifyClient.getCurrentUserProfile()).willThrow(new RuntimeException(message));
    // Then
    assertThatThrownBy(() -> underTest.getCurrentUser())
        .isExactlyInstanceOf(GetUserException.class)
        .hasMessage(UNABLE_TO_GET_USER + message);
  }

  @Test
  void itShouldThrowGetUserExceptionWhenUserMapperThrowsRuntimeException() {
    // Given
    String message = "message";
    given(appUserMapper.mapToUser(any())).willThrow(new RuntimeException(message));
    // Then
    assertThatThrownBy(() -> underTest.getCurrentUser())
        .isExactlyInstanceOf(GetUserException.class)
        .hasMessage(UNABLE_TO_GET_USER + message);
  }
}
