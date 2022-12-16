package com.ksaraev.spotifyrunning.model.user;

import com.ksaraev.spotifyrunning.client.dto.items.userprofile.UserProfileItem;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
  User toUser(UserProfileItem userProfileItem);
}
