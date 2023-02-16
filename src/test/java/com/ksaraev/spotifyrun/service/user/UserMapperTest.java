package com.ksaraev.spotifyrun.service.user;

import com.ksaraev.spotifyrun.client.items.SpotifyUserProfileItem;
import com.ksaraev.spotifyrun.exception.mapping.MappingSourceIsNullException;
import com.ksaraev.spotifyrun.model.spotify.SpotifyMapperImpl;
import com.ksaraev.spotifyrun.model.user.User;
import com.ksaraev.spotifyrun.model.user.UserMapper;
import com.ksaraev.spotifyrun.model.user.UserMapperImpl;
import com.ksaraev.spotifyrun.utils.JsonHelper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.URI;

import static com.ksaraev.spotifyrun.exception.mapping.MappingSourceIsNullException.MAPPING_SOURCE_IS_NULL_EXCEPTION_MESSAGE;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UserMapperImpl.class})
class UserMapperTest {

  @Autowired UserMapper underTest;

  @Test
  void itShouldMapSpotifyUserProfileToUser() {
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
    // When and Then
    Assertions.assertThat(
            underTest.mapToUser(JsonHelper.jsonToObject(json, SpotifyUserProfileItem.class)))
        .isEqualTo(user);
  }

  @Test
  void itShouldThrowMappingExceptionWhenSourceIsNull() {
    // Given When Then
    Assertions.assertThatThrownBy(() -> underTest.mapToUser(null))
        .isExactlyInstanceOf(MappingSourceIsNullException.class)
        .hasMessage(MAPPING_SOURCE_IS_NULL_EXCEPTION_MESSAGE);
  }
}
