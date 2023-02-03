package com.ksaraev.spotifyrun.model.playlist;

import com.ksaraev.spotifyrun.client.items.SpotifyPlaylistItem;
import com.ksaraev.spotifyrun.client.items.SpotifyPlaylistItemDetails;
import com.ksaraev.spotifyrun.model.spotify.SpotifyPlaylistDetails;
import com.ksaraev.spotifyrun.model.track.TrackMapper;
import com.ksaraev.spotifyrun.model.user.UserMapper;
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
