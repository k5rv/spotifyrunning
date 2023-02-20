package com.ksaraev.spotifyrun.service.user;

import com.ksaraev.spotifyrun.client.items.SpotifyUserProfileItem;
import com.ksaraev.spotifyrun.exception.mapping.MappingSourceIsNullException;
import com.ksaraev.spotifyrun.model.user.User;
import com.ksaraev.spotifyrun.model.user.UserMapper;
import com.ksaraev.spotifyrun.model.user.UserMapperImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.URI;

import static com.ksaraev.spotifyrun.exception.mapping.MappingSourceIsNullException.MAPPING_SOURCE_IS_NULL_EXCEPTION_MESSAGE;
import static com.ksaraev.spotifyrun.utils.JsonHelper.jsonToObject;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UserMapperImpl.class})
class UserMapperTest {

  @Autowired UserMapper underTest;

  @Test
  void itShouldMapSpotifyUserProfileItemToUser() {
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
    // Then
    Assertions.assertThat(underTest.mapToUser(userProfileItem)).isEqualTo(user);
  }

  @Test
  void itShouldThrowMappingSourceIsNullExceptionWhenUserProfileItemIsNull() {
    // Then
    Assertions.assertThatThrownBy(() -> underTest.mapToUser(null))
        .isExactlyInstanceOf(MappingSourceIsNullException.class)
        .hasMessage(MAPPING_SOURCE_IS_NULL_EXCEPTION_MESSAGE);
  }
}
