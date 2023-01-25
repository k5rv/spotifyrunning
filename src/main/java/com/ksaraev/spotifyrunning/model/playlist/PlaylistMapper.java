package com.ksaraev.spotifyrunning.model.playlist;

import com.ksaraev.spotifyrunning.client.dto.items.playlist.SpotifyPlaylistDTO;
import com.ksaraev.spotifyrunning.model.track.TrackMapper;
import com.ksaraev.spotifyrunning.model.user.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {UserMapper.class, TrackMapper.class})
public interface PlaylistMapper {

  @Mapping(target = "tracks", source = "spotifyPlaylistItemContent")
  Playlist toModel(SpotifyPlaylistDTO spotifyPlaylistDTO);
}
