package com.suddenrun.app.user;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import com.suddenrun.spotify.model.userprofile.SpotifyUserProfile;
import com.suddenrun.spotify.model.userprofile.SpotifyUserProfileItem;
import java.net.URI;

import com.suddenrun.spotify.model.SpotifyItemType;
import org.mapstruct.*;

@Mapper(componentModel = SPRING)
public interface AppUserMapper {

  default AppUser mapToEntity(SpotifyUserProfileItem userProfileItem) {
    String id = userProfileItem.getId();
    String name = userProfileItem.getName();
    return SuddenrunUser.builder().id(id).name(name).build();
  }

  default SpotifyUserProfileItem mapToDto(AppUser appUser) {
    String id = appUser.getId();
    String name = appUser.getName();
    URI uri = SpotifyItemType.USER.createUri(id);
    return SpotifyUserProfile.builder().id(id).name(name).uri(uri).build();
  }

  @ObjectFactory
  default SpotifyUserProfileItem createDto() {
    return SpotifyUserProfile.builder().build();
  }

  @ObjectFactory
  default AppUser createEntity() {
    return SuddenrunUser.builder().build();
  }
}
