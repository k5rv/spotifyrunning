package com.ksaraev.spotifyrun.app.user;

import static com.ksaraev.spotifyrun.spotify.model.SpotifyItemType.USER;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import com.ksaraev.spotifyrun.spotify.model.userprofile.SpotifyUserProfile;
import com.ksaraev.spotifyrun.spotify.model.userprofile.SpotifyUserProfileItem;
import java.net.URI;
import org.mapstruct.*;

@Mapper(componentModel = SPRING)
public interface AppUserMapper {

  default AppUser mapToEntity(SpotifyUserProfileItem userProfileItem) {
    String id = userProfileItem.getId();
    String name = userProfileItem.getName();
    return Runner.builder().id(id).name(name).build();
  }

  default SpotifyUserProfileItem mapToDto(AppUser appUser) {
    String id = appUser.getId();
    String name = appUser.getName();
    URI uri = USER.createUri(id);
    return SpotifyUserProfile.builder().id(id).name(name).uri(uri).build();
  }

  @ObjectFactory
  default SpotifyUserProfileItem createDto() {
    return SpotifyUserProfile.builder().build();
  }

  @ObjectFactory
  default AppUser createEntity() {
    return Runner.builder().build();
  }
}
