package com.suddenrun.app;

import com.suddenrun.app.user.AppUser;
import com.suddenrun.app.user.AppUserMapper;
import com.suddenrun.app.user.AppUserMapperImpl;
import com.suddenrun.app.user.Runner;
import com.suddenrun.spotify.model.SpotifyItemType;
import com.suddenrun.spotify.model.userprofile.SpotifyUserProfileItem;
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
