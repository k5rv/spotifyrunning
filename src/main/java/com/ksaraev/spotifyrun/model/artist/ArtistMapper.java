package com.ksaraev.spotifyrun.model.artist;

import com.ksaraev.spotifyrun.client.items.SpotifyArtistItem;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ArtistMapper {
  Artist toModel(SpotifyArtistItem artistItem);
}
