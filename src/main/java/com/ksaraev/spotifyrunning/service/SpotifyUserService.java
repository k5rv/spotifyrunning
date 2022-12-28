package com.ksaraev.spotifyrunning.service;

import com.ksaraev.spotifyrunning.client.dto.requests.GetSpotifyUserItemsRequest;
import com.ksaraev.spotifyrunning.model.track.SpotifyTrack;
import com.ksaraev.spotifyrunning.model.user.SpotifyUser;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public interface SpotifyUserService {

  SpotifyUser getUser();

  List<SpotifyTrack> getTopTracks();
}
