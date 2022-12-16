package com.ksaraev.spotifyrunning.model.playlist;

import com.ksaraev.spotifyrunning.client.dto.items.playlist.PlaylistItem;
import com.ksaraev.spotifyrunning.client.dto.items.playlist.PlaylistItemDetails;
import com.ksaraev.spotifyrunning.model.track.AbstractTrackMapper;
import com.ksaraev.spotifyrunning.model.user.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {UserMapper.class, AbstractTrackMapper.class})
public abstract class AbstractPlaylistMapper {

  @Mapping(target = "tracks", source = "playlistTrackItemsContent")
  public abstract Playlist toPlaylist(PlaylistItem playlistItem);

  public abstract PlaylistItemDetails toPlaylistItemDetails(
      SpotifyPlaylistDetails spotifyPlaylistDetails);
}
