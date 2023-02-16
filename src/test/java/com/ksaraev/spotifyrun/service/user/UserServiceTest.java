package com.ksaraev.spotifyrun.service.user;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.exception.http.SpotifyNotFoundException;
import com.ksaraev.spotifyrun.client.exception.http.SpotifyUnauthorizedException;
import com.ksaraev.spotifyrun.client.items.SpotifyUserProfileItem;
import com.ksaraev.spotifyrun.exception.GetUserException;
import com.ksaraev.spotifyrun.exception.UnauthorizedException;
import com.ksaraev.spotifyrun.exception.UserNotFoundException;
import com.ksaraev.spotifyrun.model.user.User;
import com.ksaraev.spotifyrun.model.user.UserMapper;
import com.ksaraev.spotifyrun.service.SpotifyUserService;
import com.ksaraev.spotifyrun.service.UserService;
import com.ksaraev.spotifyrun.utils.JsonHelper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.URI;
import java.util.Set;

import static com.ksaraev.spotifyrun.exception.GetUserException.SPOTIFY_CLIENT_RETURNED_NULL;
import static com.ksaraev.spotifyrun.exception.GetUserException.UNABLE_TO_GET_USER;
import static com.ksaraev.spotifyrun.exception.UnauthorizedException.UNAUTHORIZED;
import static com.ksaraev.spotifyrun.exception.UserNotFoundException.USER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {
  @Mock private SpotifyClient spotifyClient;
  @Mock private UserMapper userMapper;
  private SpotifyUserService underTest;
  private Validator validator;

  @BeforeAll
  public void setValidator() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    underTest = new UserService(spotifyClient, userMapper);
  }

  @Test
  void itShouldReturnUser() {
    // Given
    String id = "12122604372";
    String name = "Konstantin";
    String email = "email@gmail.com";
    URI uri = URI.create("spotify:user:12122604372");
    String json =
        "{\n"
            + "  \"display_name\":\""
            + name
            + "\",\n"
            + "  \"external_urls\":{\n"
            + "    \"spotify\":\"https://open.spotify.com/user/12122604372\"\n"
            + "  },\n"
            + "  \"followers\":{\n"
            + "    \"href\":null,\n"
            + "    \"total\":0\n"
            + "  },\n"
            + "  \"href\":\"https://api.spotify.com/v1/users/12122604372\",\n"
            + "  \"id\":\""
            + id
            + "\",\n"
            + "  \"email\":\""
            + email
            + "\",\n"
            + "  \"images\":[\n"
            + "    {\n"
            + "      \"height\":null,\n"
            + "      \"url\":\"https://www.content.com\",\n"
            + "      \"width\":null\n"
            + "    }\n"
            + "  ],\n"
            + "  \"type\":\"user\",\n"
            + "  \"uri\":\""
            + uri
            + "\"\n"
            + "}";
    User user = new User(id, name, uri, email);
    given(spotifyClient.getCurrentUserProfile())
        .willReturn(JsonHelper.jsonToObject(json, SpotifyUserProfileItem.class));
    given(userMapper.mapToUser(ArgumentMatchers.any())).willReturn(user);
    // When and Then
    assertThat(underTest.getUser())
        .isNotNull()
        .isEqualTo(user)
        .hasOnlyFields("id", "name", "email", "uri");
  }

  @Test
  void itShouldValidateWhenUserIdIsNull() {
    // Given
    User user = new User();
    user.setId(null);
    // When
    Set<ConstraintViolation<User>> violations = validator.validate(user);
    // Then
    assertThat(violations).hasSize(1);
    assertThat(new ConstraintViolationException(violations)).hasMessage("id: must not be null");
  }

  @Test
  void itShouldThrowGetUserExceptionWhenSpotifyClientReturnsNull() {
    // Given
    given(spotifyClient.getCurrentUserProfile()).willReturn(null);
    // When and Then
    assertThatThrownBy(() -> underTest.getUser())
        .isInstanceOf(GetUserException.class)
        .hasMessage(UNABLE_TO_GET_USER + ": " + SPOTIFY_CLIENT_RETURNED_NULL);
  }

  @Test
  void itShouldThrowUserNotFoundExceptionWhenClientThrowsSpotifyNotFoundException() {
    // Given
    String message = "User not found";
    given(spotifyClient.getCurrentUserProfile()).willThrow(new SpotifyNotFoundException(message));
    // When and Then
    assertThatThrownBy(() -> underTest.getUser())
        .isExactlyInstanceOf(UserNotFoundException.class)
        .hasMessage(USER_NOT_FOUND + ": " + message);
  }

  @Test
  void itShouldThrowUnauthorizedExceptionWhenClientThrowsSpotifyUnathorizedFoundException() {
    // Given
    String message = "User not found";
    given(spotifyClient.getCurrentUserProfile())
        .willThrow(new SpotifyUnauthorizedException(message));
    // When and Then
    assertThatThrownBy(() -> underTest.getUser())
        .isExactlyInstanceOf(UnauthorizedException.class)
        .hasMessage(UNAUTHORIZED + ": " + message);
  }

  @Test
  void itShouldThrowGetUserExceptionWhenClientThrowsRuntimeException() {
    // Given
    String message = "exception message";
    given(spotifyClient.getCurrentUserProfile()).willThrow(new RuntimeException(message));
    // When and Then
    assertThatThrownBy(() -> underTest.getUser())
        .isExactlyInstanceOf(GetUserException.class)
        .hasMessage(UNABLE_TO_GET_USER + ": " + message);
  }

  @Test
  void itShouldThrowGetUserExceptionWhenUserMapperThrowsRuntimeException() {
    // Given
    String json =
        """
        {
          "country":"CY",
          "display_name":"Konstantin",
          "email":"email@gmail.com",
          "explicit_content":{
            "filter_enabled":false,
            "filter_locked":false
          },
          "external_urls":{
            "spotify":"https://open.spotify.com/user/12122604372"
          },
          "followers":{
            "href":null,
            "total":0
          },
          "href":"https://api.spotify.com/v1/users/12122604372",
          "id":"12122604372",
          "images":[
            {
              "height":null,
              "url":"https://scontent-cdg2-1.xx.fbcdn.net",
              "width":null
            }
          ],
          "product":"premium",
          "type":"user",
          "uri":"spotify:user:12122604372"
        }""";
    given(spotifyClient.getCurrentUserProfile())
        .willReturn(JsonHelper.jsonToObject(json, SpotifyUserProfileItem.class));
    String runtimeExceptionMessage = "Runtime exception message";
    given(userMapper.mapToUser(any(SpotifyUserProfileItem.class)))
        .willThrow(new RuntimeException(runtimeExceptionMessage));
    // When and Then
    assertThatThrownBy(() -> underTest.getUser())
        .isExactlyInstanceOf(GetUserException.class)
        .hasMessage(UNABLE_TO_GET_USER + ": " + runtimeExceptionMessage);
  }
}
