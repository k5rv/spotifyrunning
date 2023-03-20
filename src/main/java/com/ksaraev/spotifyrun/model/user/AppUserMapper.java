package com.ksaraev.spotifyrun.model.user;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import com.ksaraev.spotifyrun.client.api.items.SpotifyUserProfileItem;
import com.ksaraev.spotifyrun.model.spotify.SpotifyMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = SPRING)
public interface AppUserMapper extends SpotifyMapper {

  @Mapping(target = "name", source = "displayName")
  AppUser mapToUser(SpotifyUserProfileItem userProfileItem);
}
