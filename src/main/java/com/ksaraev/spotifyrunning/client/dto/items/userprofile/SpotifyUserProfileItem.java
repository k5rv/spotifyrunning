package com.ksaraev.spotifyrunning.client.dto.items.userprofile;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyFollowable;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyIllustrated;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyItem;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyPublished;

@JsonDeserialize(as = SpotifyUserProfileDTO.class)
public interface SpotifyUserProfileItem
    extends SpotifyItem, SpotifyPublished, SpotifyIllustrated, SpotifyFollowable {

  String getEmail();

  String getDisplayName();

  String getCountry();

  String getProduct();
}
