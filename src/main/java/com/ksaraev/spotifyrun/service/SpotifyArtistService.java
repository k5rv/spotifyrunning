package com.ksaraev.spotifyrun.service;

import com.ksaraev.spotifyrun.model.spotify.SpotifyArtist;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface SpotifyArtistService {

  List<SpotifyArtist> getArtists(@NotNull List<String> ids);
}
