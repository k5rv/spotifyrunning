package com.ksaraev.spotifyrunning.service;

import com.ksaraev.spotifyrunning.model.artist.SpotifyArtist;
import com.ksaraev.spotifyrunning.model.track.SpotifyTrack;

import javax.validation.constraints.NotEmpty;
import java.util.List;

public interface SpotifyArtistService {

  List<SpotifyArtist> getArtists(@NotEmpty List<SpotifyTrack> tracks);
}
