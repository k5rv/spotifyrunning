package com.ksaraev.spotifyrun.app.playlist;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import com.ksaraev.spotifyrun.app.user.Runner;
import com.ksaraev.spotifyrun.app.user.RunnerMapper;
import com.ksaraev.spotifyrun.model.spotify.playlist.SpotifyPlaylistItem;
import com.ksaraev.spotifyrun.model.spotify.track.SpotifyTrackItem;
import java.util.List;
import java.util.Objects;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = SPRING, uses = RunnerMapper.class)
public interface PlaylistMapper {
  @Mapping(target = "id", source = "playlistItem.id")
  @Mapping(target = "trackIds", source = "playlistItem.tracks")
  Playlist mapToEntity(SpotifyPlaylistItem playlistItem, Runner runner);

    @Mapping(target = "id", source = "playlistItem.id")
   @Mapping(target = "trackIds", source = "playlistItem.tracks")
   Playlist updateEntity(Playlist playlist, SpotifyPlaylistItem playlistItem);

  default List<String> mapTrackIdsToList(List<SpotifyTrackItem> tracks) {
    if (tracks == null) return List.of();
    return tracks.stream().filter(Objects::nonNull).map(SpotifyTrackItem::getId).toList();
  }
}
