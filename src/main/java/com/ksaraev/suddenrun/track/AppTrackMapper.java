package com.ksaraev.suddenrun.track;

import static com.ksaraev.spotify.model.SpotifyItemType.*;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import com.ksaraev.spotify.model.track.SpotifyTrack;
import com.ksaraev.spotify.model.track.SpotifyTrackItem;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.URI;

import org.mapstruct.*;

@Mapper(componentModel = SPRING)
public interface AppTrackMapper {

  @AppTrackIdToSpotifyTrackItemUriMapper
  static URI appTrackIdToSpotifyTrackItemUriMapper(String id) {
    return TRACK.createUri(id);
  }

  AppTrack mapToEntity(SpotifyTrackItem trackItem);

  @Mapping(target = "id", source = "id")
  @Mapping(target = "uri", source = "id", qualifiedBy = AppTrackIdToSpotifyTrackItemUriMapper.class)
  @Mapping(target = "popularity", ignore = true)
  @Mapping(target = "artists", ignore = true)
  SpotifyTrackItem mapToDto(AppTrack appTrack);

  @ObjectFactory
  default SpotifyTrackItem createDto() {
    return SpotifyTrack.builder().build();
  }

  @ObjectFactory
  default AppTrack createEntity() {
    return Track.builder().build();
  }

  @Qualifier
  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.CLASS)
  @interface AppTrackIdToSpotifyTrackItemUriMapper {}
}
