package com.ksaraev.spotifyrunning.model.artist;

import com.ksaraev.spotifyrunning.client.dto.items.artist.ArtistItem;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ArtistMapper {
  Artist toArtist(ArtistItem artistItem);
}
