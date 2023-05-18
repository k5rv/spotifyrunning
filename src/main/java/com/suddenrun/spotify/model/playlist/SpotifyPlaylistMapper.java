package com.suddenrun.spotify.model.playlist;

import com.suddenrun.spotify.client.dto.SpotifyPlaylistDetailsDto;
import com.suddenrun.spotify.client.dto.SpotifyPlaylistDto;
import com.suddenrun.spotify.model.playlistdetails.SpotifyPlaylistItemDetails;
import com.suddenrun.spotify.model.track.SpotifyTrackMapper;
import com.suddenrun.spotify.model.userprofile.SpotifyUserProfileMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {SpotifyUserProfileMapper.class, SpotifyTrackMapper.class})
public interface SpotifyPlaylistMapper {

  @Mapping(target = "owner", source = "userProfileDto")
  @Mapping(target = "tracks", source = "playlistMusicDto.playlistTrackDtos")
  SpotifyPlaylist mapToModel(SpotifyPlaylistDto playlistDto);

  SpotifyPlaylistDetailsDto mapToPlaylistDetailsDto(SpotifyPlaylistItemDetails playlistDetails);
}
