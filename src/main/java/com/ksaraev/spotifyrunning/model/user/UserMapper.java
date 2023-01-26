package com.ksaraev.spotifyrunning.model.user;

import com.ksaraev.spotifyrunning.client.dto.items.userprofile.SpotifyUserProfileItem;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
  User toModel(SpotifyUserProfileItem spotifyUserProfileItem);
}
