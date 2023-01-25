package com.ksaraev.spotifyrunning.client.dto.items.playlist;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyFollowable;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyIllustrated;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyItem;
import com.ksaraev.spotifyrunning.client.dto.items.SpotifyPublished;
import com.ksaraev.spotifyrunning.client.dto.items.playlist.content.SpotifyPlaylistItemContent;
import com.ksaraev.spotifyrunning.client.dto.items.playlist.details.SpotifyPlaylistItemDetails;

@JsonDeserialize(as = SpotifyPlaylistDTO.class)
public interface SpotifyPlaylistItem
    extends SpotifyItem,
        SpotifyPublished,
        SpotifyIllustrated,
        SpotifyFollowable,
        SpotifyPlaylistItemDetails {

  String getSnapshotId();

  SpotifyPlaylistItemContent getSpotifyPlaylistItemContent();
}
