package com.ksaraev.spotifyrun.app.playlist;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import com.ksaraev.spotifyrun.app.user.RunnerMapper;
import com.ksaraev.spotifyrun.model.spotify.playlist.SpotifyPlaylistItem;
import com.ksaraev.spotifyrun.model.spotify.track.SpotifyTrackItem;
import java.util.List;
import java.util.Objects;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = SPRING, uses = RunnerMapper.class)
public interface PlaylistMapper {
  @Mapping(target = "uuid", ignore = true)
  @Mapping(target = "runner", ignore = true)
  @Mapping(target = "spotifyId", source = "id")
  @Mapping(target = "trackIds", source = "tracks")
  Playlist mapToEntity(SpotifyPlaylistItem playlistItem);

  default List<String> mapToEntityField(List<SpotifyTrackItem> spotifyTracks) {
    if (spotifyTracks == null) return List.of();
    return spotifyTracks.stream().filter(Objects::nonNull).map(SpotifyTrackItem::getId).toList();
  }
}
