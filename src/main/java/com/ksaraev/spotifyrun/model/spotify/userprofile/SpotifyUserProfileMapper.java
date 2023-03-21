package com.ksaraev.spotifyrun.model.spotify.userprofile;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import com.ksaraev.spotifyrun.client.api.items.SpotifyUserProfileDto;
import com.ksaraev.spotifyrun.model.SpotifyMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = SPRING)
public interface SpotifyUserProfileMapper extends SpotifyMapper {

  @Mapping(target = "name", source = "displayName")
  SpotifyUserProfile mapToUser(SpotifyUserProfileDto userProfileItem);
}
