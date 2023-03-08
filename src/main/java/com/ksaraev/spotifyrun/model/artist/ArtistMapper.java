package com.ksaraev.spotifyrun.model.artist;

import com.ksaraev.spotifyrun.client.items.SpotifyArtistItem;
import com.ksaraev.spotifyrun.model.spotify.SpotifyMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ArtistMapper extends SpotifyMapper {
  Artist mapToArtist(SpotifyArtistItem artistItem);
}
