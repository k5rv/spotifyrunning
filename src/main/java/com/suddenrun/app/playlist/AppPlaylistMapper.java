package com.suddenrun.app.playlist;

import static com.suddenrun.spotify.model.SpotifyItemType.*;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import com.suddenrun.app.track.AppTrackMapper;
import com.suddenrun.app.user.AppUserMapper;
import com.suddenrun.spotify.model.playlist.SpotifyPlaylist;
import com.suddenrun.spotify.model.playlist.SpotifyPlaylistItem;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.URI;
import java.util.List;

import org.mapstruct.*;

@Mapper(
    componentModel = SPRING,
    uses = {AppUserMapper.class, AppTrackMapper.class})
public interface AppPlaylistMapper {

  @AppPlaylistIdToSpotifyPlaylistItemUriMapper
  static URI appPlaylistIdToSpotifyPlaylistItemUri(String id) {
    return PLAYLIST.createUri(id);
  }

  @Mapping(target = "rejectedTracks", ignore = true)
  @Mapping(target = "customTracks", ignore = true)
  @Mapping(target = "owner", source = "playlistItem.owner")
  AppPlaylist mapToEntity(SpotifyPlaylistItem playlistItem);

  @Mapping(
      target = "uri",
      source = "id",
      qualifiedBy = AppPlaylistIdToSpotifyPlaylistItemUriMapper.class)
  @Mapping(target = "name", ignore = true)
  @Mapping(target = "isPublic", ignore = true)
  @Mapping(target = "isCollaborative", ignore = true)
  @Mapping(target = "description", ignore = true)
  SpotifyPlaylistItem mapToDto(AppPlaylist appPlaylist);

  @ObjectFactory
  default AppPlaylist createEntity() {
    return Playlist.builder()
        .tracks(List.of())
        .customTracks(List.of())
        .rejectedTracks(List.of())
        .build();
  }

  @ObjectFactory
  default SpotifyPlaylistItem createDto() {
    return SpotifyPlaylist.builder().tracks(List.of()).build();
  }

  @Qualifier
  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.CLASS)
  @interface AppPlaylistIdToSpotifyPlaylistItemUriMapper {}
}
