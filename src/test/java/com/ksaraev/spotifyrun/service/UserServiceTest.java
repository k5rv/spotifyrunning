package com.ksaraev.spotifyrun.service;

import static com.ksaraev.spotifyrun.exception.service.GetUserException.UNABLE_TO_GET_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.items.SpotifyUserProfileItem;
import com.ksaraev.spotifyrun.exception.service.GetUserException;
import com.ksaraev.spotifyrun.model.user.User;
import com.ksaraev.spotifyrun.model.user.UserMapper;
import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UserServiceTest {
  @Mock private SpotifyClient spotifyClient;
  @Mock private UserMapper userMapper;
  private SpotifyUserService underTest;

  @Captor private ArgumentCaptor<SpotifyUserProfileItem> userProfileItemArgumentCaptor;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    underTest = new UserService(spotifyClient, userMapper);
  }

  @Test
  void itShouldGetUser() {
    // Given
    String userId = "12122604372";
    String userName = "Konstantin";
    String userEmail = "email@gmail.com";
    URI userUri = URI.create("spotify:user:12122604372");

    User user = User.builder().id(userId).name(userName).email(userEmail).uri(userUri).build();

    SpotifyUserProfileItem userProfileItem =
        SpotifyUserProfileItem.builder()
            .id(userId)
            .displayName(userName)
            .uri(userUri)
            .email(userEmail)
            .build();

    given(spotifyClient.getCurrentUserProfile()).willReturn(userProfileItem);
    given(userMapper.mapToUser(userProfileItem)).willReturn(user);

    // When
    underTest.getCurrentUser();

    // Then
    verify(spotifyClient, times(1)).getCurrentUserProfile();
    then(userMapper).should().mapToUser(userProfileItemArgumentCaptor.capture());
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
    given(userMapper.mapToUser(any())).willThrow(new RuntimeException(message));
    // Then
    assertThatThrownBy(() -> underTest.getCurrentUser())
        .isExactlyInstanceOf(GetUserException.class)
        .hasMessage(UNABLE_TO_GET_USER + message);
  }
}
