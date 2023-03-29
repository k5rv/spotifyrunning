package com.ksaraev.spotifyrun.app.playlist;

import static com.ksaraev.spotifyrun.model.spotify.SpotifyItemType.*;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import com.ksaraev.spotifyrun.app.track.AppTrackMapper;
import com.ksaraev.spotifyrun.app.user.AppUserMapper;
import com.ksaraev.spotifyrun.model.spotify.playlist.SpotifyPlaylist;
import com.ksaraev.spotifyrun.model.spotify.playlist.SpotifyPlaylistItem;
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

  @Mapping(target = "owner", source = "playlistItem.owner")
  @Mapping(target = "tracks", ignore = true)
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
    return Playlist.builder().tracks(List.of()).build();
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
