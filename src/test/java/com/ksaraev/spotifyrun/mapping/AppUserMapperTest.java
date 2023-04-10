package com.ksaraev.spotifyrun.mapping;

import com.ksaraev.spotifyrun.app.user.AppUser;
import com.ksaraev.spotifyrun.app.user.AppUserMapper;
import com.ksaraev.spotifyrun.app.user.AppUserMapperImpl;
import com.ksaraev.spotifyrun.app.user.Runner;
import com.ksaraev.spotifyrun.spotify.model.SpotifyItemType;
import com.ksaraev.spotifyrun.spotify.model.userprofile.SpotifyUserProfileItem;
import java.net.URI;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AppUserMapperImpl.class})
class AppUserMapperTest {

  @Autowired AppUserMapper underTest;

  @Test
  void itShouldMapEntityToDto() {
    // Given
    String id = "12122604372";
    String name = "Konstantin";
    URI uri = SpotifyItemType.USER.createUri(id);
    AppUser appUser = Runner.builder().id(id).name(name).build();
    SpotifyUserProfileItem userProfileItem = underTest.mapToDto(appUser);
    // Then
    Assertions.assertThat(userProfileItem.getId()).isEqualTo(id);
    Assertions.assertThat(userProfileItem.getName()).isEqualTo(name);
    Assertions.assertThat(userProfileItem.getUri()).isEqualTo(uri);
  }
}
