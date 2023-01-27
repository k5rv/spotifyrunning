package com.ksaraev.spotifyrunning.model.user;

import com.ksaraev.spotifyrunning.client.items.SpotifyUserProfileItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
  @Mapping(target = "name", source = "displayName")
  User toModel(SpotifyUserProfileItem userProfileItem);
}
