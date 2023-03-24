package com.ksaraev.spotifyrun.app.playlist;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import com.ksaraev.spotifyrun.app.user.Runner;
import com.ksaraev.spotifyrun.app.user.RunnerMapper;
import com.ksaraev.spotifyrun.model.spotify.playlist.SpotifyPlaylistItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = SPRING,
    uses = {RunnerMapper.class})
public interface PlaylistMapper {
  @Mapping(target = "id", source = "playlistItem.id")
  Playlist mapToEntity(SpotifyPlaylistItem playlistItem, Runner runner);

  @Mapping(target = "id", source = "playlistItem.id")
  @Mapping(target = "tracks", source = "playlistItem.tracks")
  Playlist updateEntity(Playlist playlist, SpotifyPlaylistItem playlistItem);
}
