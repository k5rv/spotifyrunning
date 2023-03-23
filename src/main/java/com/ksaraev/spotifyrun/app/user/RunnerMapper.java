package com.ksaraev.spotifyrun.app.user;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import com.ksaraev.spotifyrun.model.spotify.userprofile.SpotifyUserProfileItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = SPRING)
public interface RunnerMapper {

  @Mapping(target = "playlists", ignore = true)
  Runner mapToEntity(SpotifyUserProfileItem userProfileItem);
}
