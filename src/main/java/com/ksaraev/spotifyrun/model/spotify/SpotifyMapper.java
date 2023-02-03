package com.ksaraev.spotifyrun.model.spotify;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SpotifyMapper {
  default <T extends SpotifyItem> List<String> toSeed(List<T> items) {
    if (CollectionUtils.isEmpty(items)) return List.of();
    return items.stream().filter(Objects::nonNull).map(SpotifyItem::getId).toList();
  }
}
