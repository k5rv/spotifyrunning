package com.ksaraev.spotifyrunning.model.artist;

import com.ksaraev.spotifyrunning.client.items.SpotifyArtistItem;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ArtistMapper {
  Artist toModel(SpotifyArtistItem artistItem);
}
