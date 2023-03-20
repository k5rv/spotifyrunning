package com.ksaraev.spotifyrun.mapping;

import static com.ksaraev.spotifyrun.exception.mapper.NullMappingSourceException.MAPPING_SOURCE_IS_NULL_EXCEPTION_MESSAGE;

import com.ksaraev.spotifyrun.client.api.items.SpotifyUserProfileItem;
import com.ksaraev.spotifyrun.exception.mapper.NullMappingSourceException;
import com.ksaraev.spotifyrun.model.user.AppUserMapper;
import com.ksaraev.spotifyrun.model.user.AppUserMapperImpl;
import com.ksaraev.spotifyrun.model.user.SpotifyUser;
import com.ksaraev.spotifyrun.model.user.AppUserMapperImpl;
import com.ksaraev.spotifyrun.utils.SpotifyHelper;
import java.net.URL;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AppUserMapperImpl.class})
class AppAppUserMapperTest {

  @Autowired AppUserMapper underTest;

  @Test
  void itShouldMapSpotifyUserProfileItemToUser() throws Exception {
    // Given
    SpotifyUser user = SpotifyHelper.getUser();

    SpotifyUserProfileItem userProfileItem =
        SpotifyUserProfileItem.builder()
            .id(user.getId())
            .displayName(user.getName())
            .uri(user.getUri())
            .email(user.getEmail())
            .country("CC")
            .explicitContent(Map.of("filter_enabled", false, "filter_locked", false))
            .externalUrls(Map.of("spotify", "https://open.spotify.com/user/12122604372"))
            .followers(Map.of("href", "", "total", 0))
            .href(new URL("https://api.spotify.com/v1/users/12122604372"))
            .images(
                List.of(
                    Map.of(
                        "height",
                        "",
                        "url",
                        new URL("https://scontent-cdg2-1.xx.fbcdn.net"),
                        "width",
                        "")))
            .product("premium")
            .type("user")
            .build();

    // Then
    Assertions.assertThat(underTest.mapToUser(userProfileItem))
        .isNotNull()
        .isEqualTo(user)
        .hasOnlyFields("id", "name", "email", "uri");
  }

  //
  @Test
  void itShouldThrowNullMappingSourceExceptionWhenUserProfileItemIsNull() {
    // Then
    Assertions.assertThatThrownBy(() -> underTest.mapToUser(null))
        .isExactlyInstanceOf(NullMappingSourceException.class)
        .hasMessage(MAPPING_SOURCE_IS_NULL_EXCEPTION_MESSAGE);
  }
}
