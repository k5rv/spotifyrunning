package com.ksaraev.spotifyrun.model.user;

import com.ksaraev.spotifyrun.client.items.SpotifyUserProfileItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
  @Mapping(target = "name", source = "displayName")
  User toModel(SpotifyUserProfileItem userProfileItem);
}
