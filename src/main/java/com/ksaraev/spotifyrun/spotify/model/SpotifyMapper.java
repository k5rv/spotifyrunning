package com.ksaraev.spotifyrun.spotify.model;

import static com.ksaraev.spotifyrun.spotify.model.MappingSourceIsNullException.MAPPING_SOURCE_IS_NULL;

import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SpotifyMapper {
  @BeforeMapping
  default <T> void throwIfNull(T source) {
    if (source == null) {
      throw new MappingSourceIsNullException();
    }
  }
}
