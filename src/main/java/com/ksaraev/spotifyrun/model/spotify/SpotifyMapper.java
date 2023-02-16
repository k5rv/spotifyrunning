package com.ksaraev.spotifyrun.model.spotify;

import com.ksaraev.spotifyrun.exception.mapping.MappingSourceIsNullException;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import static com.ksaraev.spotifyrun.exception.mapping.MappingSourceIsNullException.MAPPING_SOURCE_IS_NULL_EXCEPTION_MESSAGE;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SpotifyMapper {
  @BeforeMapping
  default <T> void throwIfNull(T source) {
    if (source == null) {
      throw new MappingSourceIsNullException(MAPPING_SOURCE_IS_NULL_EXCEPTION_MESSAGE);
    }
  }
}
