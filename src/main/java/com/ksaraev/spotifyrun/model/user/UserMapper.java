package com.ksaraev.spotifyrun.model.user;

import com.ksaraev.spotifyrun.client.items.SpotifyUserProfileItem;
import com.ksaraev.spotifyrun.model.spotify.SpotifyMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper extends SpotifyMapper {

  @Mapping(target = "name", source = "displayName")
  User mapToUser(SpotifyUserProfileItem userProfileItem);
}
