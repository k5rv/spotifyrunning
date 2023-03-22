package com.ksaraev.spotifyrun.model.spotify.playlist;

import com.ksaraev.spotifyrun.client.api.SpotifyPlaylistDetailsDto;
import com.ksaraev.spotifyrun.client.api.SpotifyPlaylistDto;
import com.ksaraev.spotifyrun.model.SpotifyMapper;
import com.ksaraev.spotifyrun.model.spotify.playlistdetails.SpotifyPlaylistItemDetails;
import com.ksaraev.spotifyrun.model.spotify.track.SpotifyTrackMapper;
import com.ksaraev.spotifyrun.model.spotify.userprofile.SpotifyUserProfileMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {SpotifyUserProfileMapper.class, SpotifyTrackMapper.class})
public interface SpotifyPlaylistMapper extends SpotifyMapper {

  @Mapping(target = "owner", source = "userProfileItem")
  @Mapping(target = "tracks", source = "playlistItemMusic.playlistItemTracks")
  SpotifyPlaylist mapToPlaylist(SpotifyPlaylistDto playlistItem);

  SpotifyPlaylistDetailsDto mapToPlaylistItemDetails(SpotifyPlaylistItemDetails playlistDetails);
}
