package com.ksaraev.spotifyrun.app.runner;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import com.ksaraev.spotifyrun.model.spotify.userprofile.SpotifyUserProfileItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = SPRING)
public interface RunnerMapper {

  @Mapping(target = "uuid", ignore = true)
  @Mapping(target = "spotifyId", source = "id")
  Runner mapToEntity(SpotifyUserProfileItem userProfileItem);
}
