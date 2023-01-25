package com.ksaraev.spotifyrunning.client.dto.items.playlist.content;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyPreview;
import com.ksaraev.spotifyrunning.client.dto.items.track.SpotifyTrackItem;
import com.ksaraev.spotifyrunning.client.dto.items.userprofile.SpotifyUserProfileItem;

@JsonDeserialize(as = SpotifyAddableTrackDTO.class)
public interface SpotifyAddableTrackItem extends SpotifyPreview {

  String getAddedAt();

  SpotifyUserProfileItem getAddedBy();

  Boolean getIsLocal();

  SpotifyTrackItem getSpotifyTrackItem();
}
