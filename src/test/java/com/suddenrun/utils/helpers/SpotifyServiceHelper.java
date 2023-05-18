package com.suddenrun.utils.helpers;

import static com.suddenrun.utils.helpers.SpotifyResourceHelper.*;

import com.suddenrun.spotify.model.SpotifyItem;
import com.suddenrun.spotify.model.artist.SpotifyArtist;
import com.suddenrun.spotify.model.artist.SpotifyArtistItem;
import com.suddenrun.spotify.model.playlist.SpotifyPlaylist;
import com.suddenrun.spotify.model.playlist.SpotifyPlaylistItem;
import com.suddenrun.spotify.model.playlistdetails.SpotifyPlaylistDetails;
import com.suddenrun.spotify.model.playlistdetails.SpotifyPlaylistItemDetails;
import com.suddenrun.spotify.model.track.SpotifyTrack;
import com.suddenrun.spotify.model.track.SpotifyTrackItem;
import com.suddenrun.spotify.model.trackfeatures.SpotifyTrackFeatures;
import com.suddenrun.spotify.model.trackfeatures.SpotifyTrackItemFeatures;
import com.suddenrun.spotify.model.userprofile.SpotifyUserProfile;
import com.suddenrun.spotify.model.userprofile.SpotifyUserProfileItem;
import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class SpotifyServiceHelper {

  public static SpotifyUserProfileItem getUserProfile() {
    String id = getRandomId();
    String name = getRandomName();
    URI uri = USER.getUri(id);
    String email = getRandomEmail();
    return SpotifyUserProfile.builder().id(id).name(name).uri(uri).email(email).build();
  }

  public static SpotifyArtistItem getArtist() {
    String id = getRandomId();
    String name = getRandomName();
    URI uri = ARTIST.getUri(id);
    List<String> genres = getRandomGenres();
    return SpotifyArtist.builder().id(id).name(name).uri(uri).genres(genres).build();
  }

  public static SpotifyTrackItem getTrack() {
    String id = getRandomId();
    String name = getRandomName();
    URI uri = TRACK.getUri(id);
    Integer popularity = getRandomPopularity();
    List<SpotifyArtistItem> artists = getArtists(1);
    return SpotifyTrack.builder()
        .id(id)
        .name(name)
        .uri(uri)
        .popularity(popularity)
        .artists(artists)
        .build();
  }

  public static SpotifyPlaylistItemDetails getPlaylistDetails() {
    String name = getRandomName();
    String description = getRandomDescription();
    Boolean isPublic = true;
    Boolean isCollaborative = false;
    return SpotifyPlaylistDetails.builder()
        .name(name)
        .description(description)
        .isPublic(isPublic)
        .isCollaborative(isCollaborative)
        .build();
  }

  public static SpotifyPlaylistItem getPlaylist() {
    String id = getRandomId();
    String name = getRandomName();
    String description = getRandomDescription();
    URI uri = PLAYLIST.getUri(id);
    SpotifyUserProfileItem user = getUserProfile();
    String snapshotId = getRandomSnapshotId();
    Boolean isPublic = true;
    Boolean isCollaborative = false;
    return SpotifyPlaylist.builder()
        .id(id)
        .name(name)
        .uri(uri)
        .description(description)
        .isPublic(isPublic)
        .isCollaborative(isCollaborative)
        .owner(user)
        .snapshotId(snapshotId)
        .build();
  }

  public static SpotifyTrackItemFeatures getSpotifyTrackFeatures() {
    BigDecimal minTempo = new BigDecimal(120);
    BigDecimal maxTempo = new BigDecimal(140);
    BigDecimal minEnergy = new BigDecimal("0.65");
    return SpotifyTrackFeatures.builder()
        .minTempo(minTempo)
        .maxTempo(maxTempo)
        .minEnergy(minEnergy)
        .build();
  }

  public static List<SpotifyArtistItem> getArtists(Integer size) {
    return getSpotifyItems(size, SpotifyArtistItem.class);
  }

  public static List<SpotifyTrackItem> getTracks(Integer size) {
    return getSpotifyItems(size, SpotifyTrackItem.class);
  }

  private static <T extends SpotifyItem> T getSpotifyItem(Class<T> type) {
    if (type.isAssignableFrom(SpotifyUserProfileItem.class)) return type.cast(getUserProfile());
    if (type.isAssignableFrom(SpotifyTrackItem.class)) return type.cast(getTrack());
    if (type.isAssignableFrom(SpotifyArtistItem.class)) return type.cast(getArtist());
    if (type.isAssignableFrom(SpotifyPlaylistItemDetails.class))
      return type.cast(getPlaylistDetails());
    if (type.isAssignableFrom(SpotifyPlaylistItem.class)) return type.cast(getPlaylist());
    throw new UnsupportedOperationException("not supported type:" + type);
  }

  private static <T extends SpotifyItem> List<T> getSpotifyItems(Integer size, Class<T> type) {
    List<T> items = new ArrayList<>();
    IntStream.range(0, size).forEach(index -> items.add(index, getSpotifyItem(type)));
    return items;
  }
}
