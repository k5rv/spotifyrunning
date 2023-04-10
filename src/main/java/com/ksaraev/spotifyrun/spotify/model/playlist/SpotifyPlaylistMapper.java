package com.ksaraev.spotifyrun.spotify.model.playlist;

import com.ksaraev.spotifyrun.client.dto.SpotifyPlaylistDetailsDto;
import com.ksaraev.spotifyrun.client.dto.SpotifyPlaylistDto;
import com.ksaraev.spotifyrun.spotify.model.playlistdetails.SpotifyPlaylistItemDetails;
import com.ksaraev.spotifyrun.spotify.model.track.SpotifyTrackMapper;
import com.ksaraev.spotifyrun.spotify.model.userprofile.SpotifyUserProfileMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {SpotifyUserProfileMapper.class, SpotifyTrackMapper.class})
public interface SpotifyPlaylistMapper {

  @Mapping(target = "owner", source = "userProfileItem")
  @Mapping(target = "tracks", source = "playlistItemMusic.playlistItemTracks")
  SpotifyPlaylist mapToPlaylist(SpotifyPlaylistDto playlistItem);

  SpotifyPlaylistDetailsDto mapToPlaylistItemDetails(SpotifyPlaylistItemDetails playlistDetails);
}
