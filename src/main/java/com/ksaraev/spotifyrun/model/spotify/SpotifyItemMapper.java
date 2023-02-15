package com.ksaraev.spotifyrun.model.spotify;

import com.ksaraev.spotifyrun.exception.mapping.MappingSourceIsNullException;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

import static com.ksaraev.spotifyrun.exception.mapping.MappingSourceIsNullException.MAPPING_SOURCE_IS_NULL_EXCEPTION_MESSAGE;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SpotifyItemMapper {
  @BeforeMapping
  default <T> void throwIfNull(T source) {
    if (source == null) {
      throw new MappingSourceIsNullException(MAPPING_SOURCE_IS_NULL_EXCEPTION_MESSAGE);
    }
  }

  default <T extends SpotifyItem> List<String> toSeed(List<T> items) {
    if (CollectionUtils.isEmpty(items)) return List.of();
    return items.stream().filter(Objects::nonNull).map(SpotifyItem::getId).toList();
  }
}
