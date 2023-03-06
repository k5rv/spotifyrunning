package com.ksaraev.spotifyrun.model.user;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import com.ksaraev.spotifyrun.client.items.SpotifyUserProfileItem;
import com.ksaraev.spotifyrun.model.spotify.SpotifyMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = SPRING)
public interface UserMapper extends SpotifyMapper {

  @Mapping(target = "name", source = "displayName")
  User mapToUser(SpotifyUserProfileItem userProfileItem);
}
