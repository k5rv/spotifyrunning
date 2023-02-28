package com.ksaraev.spotifyrun.service.user;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.items.SpotifyUserProfileItem;
import com.ksaraev.spotifyrun.exception.service.GetUserException;
import com.ksaraev.spotifyrun.model.spotify.SpotifyUser;
import com.ksaraev.spotifyrun.model.user.User;
import com.ksaraev.spotifyrun.model.user.UserMapper;
import com.ksaraev.spotifyrun.service.SpotifyUserService;
import com.ksaraev.spotifyrun.service.UserService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.URI;
import java.util.Set;

import static com.ksaraev.spotifyrun.exception.service.GetUserException.UNABLE_TO_GET_USER;
import static com.ksaraev.spotifyrun.utils.JsonHelper.jsonToObject;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class UserServiceTest {
  @Mock private SpotifyClient spotifyClient;
  @Mock private UserMapper userMapper;
  private SpotifyUserService underTest;
  private Validator validator;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    validator = Validation.buildDefaultValidatorFactory().getValidator();
    underTest = new UserService(spotifyClient, userMapper);
  }

  @Test
  void itShouldGetUser() {
    // Given
    String id = "12122604372";
    String name = "Konstantin";
    String email = "email@gmail.com";
    URI uri = URI.create("spotify:user:12122604372");
    User user = User.builder().id(id).name(name).email(email).uri(uri).build();
    String spotifyUserProfileItemJson =
        """
         {
           "country": "CC",
           "display_name": "%s",
           "email": "%s",
           "explicit_content": {
             "filter_enabled": false,
             "filter_locked": false
           },
           "external_urls": {
             "spotify": "https://open.spotify.com/user/12122604372"
           },
           "followers": {
             "href": null,
             "total": 0
           },
           "href": "https://api.spotify.com/v1/users/12122604372",
           "id": "%s",
           "images": [
             {
               "height": null,
               "url": "https://scontent-cdg2-1.xx.fbcdn.net",
               "width": null
             }
           ],
           "product": "premium",
           "type": "user",
           "uri": "%s"
         }
         """
            .formatted(name, email, id, uri);
    SpotifyUserProfileItem userProfileItem =
        jsonToObject(spotifyUserProfileItemJson, SpotifyUserProfileItem.class);
    given(spotifyClient.getCurrentUserProfile()).willReturn(userProfileItem);
    given(userMapper.mapToUser(ArgumentMatchers.any())).willReturn(user);
    // Then
    assertThat(underTest.getCurrentUser())
        .isNotNull()
        .isEqualTo(user)
        .hasOnlyFields("id", "name", "email", "uri");
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      nullValues = "null",
      textBlock =
          """
           null       |Konstantin|email@gmail.com|spotify:user:12122604372|id: must not be null
           12122604372|null      |email@gmail.com|spotify:user:12122604372|name: must not be empty
           12122604372|Konstantin|email@         |spotify:user:12122604372|email: must be a well-formed email address
           12122604372|Konstantin|email@gmail.com|null                    |uri: must not be null
           """)
  void itShouldDetectUserConstraintViolations(
      String id, String name, String email, URI uri, String message) {
    // Given
    User user = User.builder().id(id).name(name).email(email).uri(uri).build();
    // When
    Set<ConstraintViolation<SpotifyUser>> constraintViolations = validator.validate(user);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations)).hasMessage(message);
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock =
          """
           "type": "user"              |"email": "email@gmail.com"|"id": "12122604372"|"uri": "spotify:user:12122604372"|displayName: must not be empty
           "display_name": "Konstantin"|"email": "@gmail.com"     |"id": "12122604372"|"uri": "spotify:user:12122604372"|email: must be a well-formed email address
           "display_name": "Konstantin"|"email": "email@gmail.com"|"type": "user"     |"uri": "spotify:user:12122604372"|id: must not be null
           "display_name": "Konstantin"|"email": "email@gmail.com"|"id": "12122604372"|"type": "user"                   |uri: must not be null
           """)
  void itShouldDetectSpotifyUserProfileItemConstraintViolations(
      String displayNameJsonKeyValue,
      String emailJsonKeyValue,
      String idJsonKeyValue,
      String uriJsonKeyValue,
      String message) {
    // Given
    String spotifyUserProfileItemJson =
        """
         {
           "country": "CY",
           %s,
           %s,
           "explicit_content": {
             "filter_enabled": false,
             "filter_locked": false
           },
           "external_urls": {
             "spotify": "https://open.spotify.com/user/12122604372"
           },
           "followers": {
             "href": null,
             "total": 0
           },
           "href": "https://api.spotify.com/v1/users/12122604372",
           %s,
           "images": [
             {
               "height": null,
               "url": "https://scontent-cdg2-1.xx.fbcdn.net",
               "width": null
             }
           ],
           "product": "premium",
           %s
         }
         """
            .formatted(displayNameJsonKeyValue, emailJsonKeyValue, idJsonKeyValue, uriJsonKeyValue);
    SpotifyUserProfileItem spotifyUserProfileItem =
        jsonToObject(spotifyUserProfileItemJson, SpotifyUserProfileItem.class);
    // When
    Set<ConstraintViolation<SpotifyUserProfileItem>> constraintViolations =
        validator.validate(spotifyUserProfileItem);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations)).hasMessage(message);
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
    given(spotifyClient.getCurrentUserProfile()).willReturn(null);
    given(userMapper.mapToUser(any())).willThrow(new RuntimeException(message));
    // Then
    assertThatThrownBy(() -> underTest.getCurrentUser())
        .isExactlyInstanceOf(GetUserException.class)
        .hasMessage(UNABLE_TO_GET_USER + message);
  }
}
