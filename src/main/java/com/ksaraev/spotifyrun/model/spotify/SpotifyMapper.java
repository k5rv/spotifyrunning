package com.ksaraev.spotifyrun.model.spotify;

import static com.ksaraev.spotifyrun.exception.mapper.NullMappingSourceException.MAPPING_SOURCE_IS_NULL_EXCEPTION_MESSAGE;

import com.ksaraev.spotifyrun.exception.mapper.NullMappingSourceException;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SpotifyMapper {
  @BeforeMapping
  default <T> void throwIfNull(T source) {
    if (source == null) {
      throw new NullMappingSourceException(MAPPING_SOURCE_IS_NULL_EXCEPTION_MESSAGE);
    }
  }
}
