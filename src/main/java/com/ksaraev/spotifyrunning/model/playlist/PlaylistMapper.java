package com.ksaraev.spotifyrunning.model.playlist;

import com.ksaraev.spotifyrunning.client.items.SpotifyPlaylistItem;
import com.ksaraev.spotifyrunning.client.items.SpotifyPlaylistItemDetails;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyPlaylistDetails;
import com.ksaraev.spotifyrunning.model.track.TrackMapper;
import com.ksaraev.spotifyrunning.model.user.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {UserMapper.class, TrackMapper.class})
public interface PlaylistMapper {

  @Mapping(target = "owner", source = "userProfileItem")
  @Mapping(target = "tracks", source = "playlistItemMusic")
  Playlist toModel(SpotifyPlaylistItem playlistItem);

  SpotifyPlaylistItemDetails toSpotifyItem(SpotifyPlaylistDetails playlistDetails);
}
