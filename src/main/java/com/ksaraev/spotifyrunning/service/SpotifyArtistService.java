package com.ksaraev.spotifyrunning.service;

import com.ksaraev.spotifyrunning.model.spotify.SpotifyArtist;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface SpotifyArtistService {

  List<SpotifyArtist> getArtists(@NotNull List<String> ids);
}
