package com.ksaraev.spotifyrunning.client.dto.items.artist;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyFollowable;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyItem;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyPopularity;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyPublished;

import java.util.List;

@JsonDeserialize(as = SpotifyArtistDTO.class)
public interface SpotifyArtistItem
    extends SpotifyItem, SpotifyPublished, SpotifyFollowable, SpotifyPopularity {

  List<String> getGenres();
}
