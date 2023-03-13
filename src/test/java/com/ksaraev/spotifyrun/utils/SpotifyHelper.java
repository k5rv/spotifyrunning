package com.ksaraev.spotifyrun.utils;

import static com.ksaraev.spotifyrun.utils.SpotifyHelper.SpotifyItemType.*;
import static java.util.concurrent.ThreadLocalRandom.*;

import com.ksaraev.spotifyrun.client.api.GetRecommendationsRequest;
import com.ksaraev.spotifyrun.client.api.GetUserTopTracksRequest;
import com.ksaraev.spotifyrun.client.api.GetUserTopTracksResponse;
import com.ksaraev.spotifyrun.client.api.items.*;
import com.ksaraev.spotifyrun.model.artist.Artist;
import com.ksaraev.spotifyrun.model.playlist.Playlist;
import com.ksaraev.spotifyrun.model.playlist.PlaylistDetails;
import com.ksaraev.spotifyrun.model.spotify.*;
import com.ksaraev.spotifyrun.model.track.Track;
import com.ksaraev.spotifyrun.model.track.TrackFeatures;
import com.ksaraev.spotifyrun.model.user.User;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.RandomStringUtils;

public class SpotifyHelper {

  public static SpotifyUser getUser() {
    String id = getRandomId();
    String name = getRandomName();
    URI uri = USER.getUri(id);
    String email = getRandomEmail();
    return User.builder().id(id).name(name).uri(uri).email(email).build();
  }

  public static SpotifyArtist getArtist() {
    String id = getRandomId();
    String name = getRandomName();
    URI uri = ARTIST.getUri(id);
    List<String> genres = getRandomGenres();
    return Artist.builder().id(id).name(name).uri(uri).genres(genres).build();
  }

  public static SpotifyTrack getTrack() {
    String id = getRandomId();
    String name = getRandomName();
    URI uri = TRACK.getUri(id);
    Integer popularity = getRandomPopularity();
    List<SpotifyArtist> artists = getArtists(1);
    return Track.builder()
        .id(id)
        .name(name)
        .uri(uri)
        .popularity(popularity)
        .artists(artists)
        .build();
  }

  public static SpotifyPlaylistDetails getPlaylistDetails() {
    String name = getRandomName();
    String description = getRandomDescription();
    Boolean isPublic = true;
    Boolean isCollaborative = false;
    return PlaylistDetails.builder()
        .name(name)
        .description(description)
        .isPublic(isPublic)
        .isCollaborative(isCollaborative)
        .build();
  }

  public static SpotifyPlaylist getPlaylist() {
    String id = getRandomId();
    String name = getRandomName();
    String description = getRandomDescription();
    URI uri = PLAYLIST.getUri(id);
    SpotifyUser user = getUser();
    String snapshotId = getRandomSnapshotId();
    Boolean isPublic = true;
    Boolean isCollaborative = false;
    return Playlist.builder()
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

  public static SpotifyTrackFeatures getSpotifyTrackFeatures() {
    BigDecimal minTempo = new BigDecimal(120);
    BigDecimal maxTempo = new BigDecimal(140);
    BigDecimal minEnergy = new BigDecimal("0.65");
    return TrackFeatures.builder()
        .minTempo(minTempo)
        .maxTempo(maxTempo)
        .minEnergy(minEnergy)
        .build();
  }

  public static SpotifyUserProfileItem getUserProfileItem() {
    String id = getRandomId();
    String displayName = getRandomName();
    URI uri = USER.getUri(id);
    String email = getRandomEmail();
    Map<String, Object> explicitContent = getExplicitContent();
    Map<String, Object> followers = getFollowers();
    Map<String, Object> externalUrls = getExternalUrls(USER, id);
    List<Map<String, Object>> images = getImages();
    String country = getCountry();
    String product = getProduct();
    URL href = getHref(USER, id);
    return SpotifyUserProfileItem.builder()
        .id(getRandomId())
        .displayName(displayName)
        .uri(uri)
        .email(email)
        .explicitContent(explicitContent)
        .followers(followers)
        .externalUrls(externalUrls)
        .images(images)
        .country(country)
        .product(product)
        .href(href)
        .build();
  }

  public static SpotifyTrackItem getTrackItem() {
    String id = getRandomId();
    String name = getRandomName();
    URI uri = TRACK.getUri(id);
    SpotifyAlbumItem albumItem = getAlbumItem();
    SpotifyAlbumItem sourceAlbumItem = getAlbumItem();
    URL previewURL = getHref(TRACK, id);
    List<SpotifyArtistItem> artistItems = getArtistItems(1);
    Integer popularity = getRandomPopularity();
    String type = ARTIST.getType();
    URL href = getHref(ARTIST, id);
    Boolean explicit = true;
    Boolean episode = false;
    Boolean track = true;
    Boolean isLocal = false;
    Boolean isPlayable = true;
    List<String> availableMarkets = getAvailableMarkets();
    Integer discNumber = 5;
    Integer durationMs = 18000;
    Integer trackNumber = 2;
    Map<String, Object> externalUrls = getExternalUrls(TRACK, id);
    Map<String, Object> externalIds = getExternalIds(TRACK, id);
    return SpotifyTrackItem.builder()
        .id(id)
        .name(name)
        .uri(uri)
        .albumItem(albumItem)
        .sourceAlbumItem(sourceAlbumItem)
        .previewUrl(previewURL)
        .artistItems(artistItems)
        .popularity(popularity)
        .type(type)
        .href(href)
        .explicit(explicit)
        .episode(episode)
        .track(track)
        .isLocal(isLocal)
        .isPlayable(isPlayable)
        .availableMarkets(availableMarkets)
        .discNumber(discNumber)
        .durationMs(durationMs)
        .trackNumber(trackNumber)
        .externalUrls(externalUrls)
        .externalIds(externalIds)
        .build();
  }

  public static SpotifyPlaylistItemDetails getPlaylistItemDetails() {
    String name = getRandomName();
    String description = getRandomDescription();
    Boolean isPublic = true;
    Boolean isCollaborative = false;
    return SpotifyPlaylistItemDetails.builder()
        .name(name)
        .description(description)
        .isPublic(isPublic)
        .isCollaborative(isCollaborative)
        .build();
  }

  public static SpotifyAlbumItem getAlbumItem() {
    String id = getRandomId();
    String name = getRandomName();
    URI uri = ALBUM.getUri(id);
    String type = ALBUM.getType();
    URL href = getHref(ALBUM, id);
    String albumType = getAlbumType();
    String albumGroup = getAlbumGroup();
    Integer totalTracks = 50;
    String releaseDate = getReleaseDate();
    String precisionDate = getPrecisionDate();
    List<String> availableMarkets = getAvailableMarkets();
    List<SpotifyArtistItem> artistItems = getArtistItems(1);
    List<Map<String, Object>> restrictions = getRestrictions();
    Map<String, Object> externalUrls = getExternalUrls(ARTIST, id);
    List<Map<String, Object>> images = getImages();
    return SpotifyAlbumItem.builder()
        .id(id)
        .name(name)
        .uri(uri)
        .type(type)
        .href(href)
        .albumType(albumType)
        .albumGroup(albumGroup)
        .totalTracks(totalTracks)
        .releaseDate(releaseDate)
        .releaseDatePrecision(precisionDate)
        .availableMarkets(availableMarkets)
        .artistItems(artistItems)
        .restrictions(restrictions)
        .externalUrls(externalUrls)
        .images(images)
        .build();
  }

  public static SpotifyArtistItem getArtistItem() {
    String id = getRandomId();
    String name = getRandomName();
    Integer popularity = getRandomPopularity();
    List<String> genres = getRandomGenres();
    String type = ARTIST.getType();
    URI uri = ARTIST.getUri(id);
    URL href = getHref(ARTIST, id);
    Map<String, Object> followers = getFollowers();
    Map<String, Object> externalUrls = getExternalUrls(ARTIST, id);
    List<Map<String, Object>> images = getImages();
    return SpotifyArtistItem.builder()
        .id(id)
        .name(name)
        .popularity(popularity)
        .genres(genres)
        .type(type)
        .uri(uri)
        .href(href)
        .followers(followers)
        .externalUrls(externalUrls)
        .images(images)
        .build();
  }

  public static SpotifyPlaylistItem getPlaylistItem() {
    return getPlaylistItemWithMusic(null);
    /*    String id = getRandomId();
    String name = getRandomName();
    URI uri = PLAYLIST.getUri(id);
    SpotifyUserProfileItem userProfileItem = getUserProfileItem();
    String snapshotId = getRandomSnapshotId();
    String description = getRandomDescription();
    String type = PLAYLIST.getType();
    Boolean isPublic = true;
    Boolean isCollaborative = false;
    String primaryColor = getPrimaryColor();
    Map<String, Object> followers = getFollowers();
    Map<String, Object> externalUrls = getExternalUrls(PLAYLIST, id);
    List<Map<String, Object>> images = getImages();
    URL href = getHref(PLAYLIST, id);
    return SpotifyPlaylistItem.builder()
        .id(id)
        .name(name)
        .uri(uri)
        .userProfileItem(userProfileItem)
        .snapshotId(snapshotId)
        .description(description)
        .type(type)
        .isCollaborative(isCollaborative)
        .isPublic(isPublic)
        .primaryColor(primaryColor)
        .followers(followers)
        .externalUrls(externalUrls)
        .images(images)
        .href(href)
        .build();*/
  }

  public static SpotifyPlaylistItem getPlaylistItemWithMusic(SpotifyPlaylistItemMusic playlistItemMusic) {
    String id = getRandomId();
    String name = getRandomName();
    URI uri = PLAYLIST.getUri(id);
    SpotifyUserProfileItem userProfileItem = getUserProfileItem();
    String snapshotId = getRandomSnapshotId();
    String description = getRandomDescription();
    String type = PLAYLIST.getType();
    Boolean isPublic = true;
    Boolean isCollaborative = false;
    String primaryColor = getPrimaryColor();
    Map<String, Object> followers = getFollowers();
    Map<String, Object> externalUrls = getExternalUrls(PLAYLIST, id);
    List<Map<String, Object>> images = getImages();
    URL href = getHref(PLAYLIST, id);
    return SpotifyPlaylistItem.builder()
        .id(id)
        .name(name)
        .uri(uri)
        .userProfileItem(userProfileItem)
        .snapshotId(snapshotId)
        .description(description)
        .type(type)
        .isCollaborative(isCollaborative)
        .isPublic(isPublic)
        .primaryColor(primaryColor)
        .followers(followers)
        .externalUrls(externalUrls)
        .images(images)
        .href(href)
        .playlistItemMusic(playlistItemMusic)
        .build();
  }

  public static SpotifyPlaylistItemTrack getSpotifyPlaylistItemTrack() {
    SpotifyTrackItem trackItem = getTrackItem();
    String addedAt = "2020-12-04T14:14:36Z";
    SpotifyUserProfileItem addedBy = getUserProfileItem();
    Boolean isLocal = false;
    String primaryColor = getPrimaryColor();
    Map<String, Object> videoThumbnail = getVideoThumbNail(trackItem.id());
    return SpotifyPlaylistItemTrack.builder()
        .trackItem(trackItem)
        .addedBy(addedBy)
        .addedAt(addedAt)
        .isLocal(isLocal)
        .primaryColor(primaryColor)
        .videoThumbnail(videoThumbnail)
        .build();
  }

  public static GetRecommendationsRequest.TrackFeatures getRecommendationRequestTrackFeatures() {
    BigDecimal minTempo = new BigDecimal(120);
    BigDecimal maxTempo = new BigDecimal(140);
    BigDecimal minEnergy = new BigDecimal("0.65");
    return GetRecommendationsRequest.TrackFeatures.builder()
        .minTempo(minTempo)
        .maxTempo(maxTempo)
        .minEnergy(minEnergy)
        .build();
  }

  public static GetUserTopTracksRequest createGetUserTopTracksRequest() {
    Integer offset = 0;
    Integer limit = 50;
    GetUserTopTracksRequest.TimeRange timeRange = GetUserTopTracksRequest.TimeRange.SHORT_TERM;
    return GetUserTopTracksRequest.builder()
        .offset(offset)
        .limit(limit)
        .timeRange(timeRange)
        .build();
  }

  public static GetUserTopTracksResponse createGetUserTopTracksResponse(
      List<SpotifyTrackItem> trackItems) throws Exception {
    URL href =
        new URL("https://api.spotify.com/v1/me/top/tracks?limit=1&offset=0&time_range=short_term");
    Integer offset = 0;
    Integer total = 50;
    Integer limit = 50;
    return GetUserTopTracksResponse.builder()
        .href(href)
        .trackItems(trackItems)
        .limit(limit)
        .offset(offset)
        .total(1)
        .next(null)
        .previous(null)
        .total(total)
        .build();
  }

  public static List<SpotifyArtist> getArtists(Integer size) {
    return getSpotifyItems(size, SpotifyArtist.class);
  }

  public static List<SpotifyTrack> getTracks(Integer size) {
    return getSpotifyItems(size, SpotifyTrack.class);
  }

  private static <T extends SpotifyItem> T getSpotifyItem(Class<T> type) {
    if (type.isAssignableFrom(SpotifyUser.class)) return type.cast(getUser());
    if (type.isAssignableFrom(SpotifyTrack.class)) return type.cast(getTrack());
    if (type.isAssignableFrom(SpotifyArtist.class)) return type.cast(getArtist());
    if (type.isAssignableFrom(SpotifyPlaylistDetails.class)) return type.cast(getPlaylistDetails());
    if (type.isAssignableFrom(SpotifyPlaylist.class)) return type.cast(getPlaylist());
    throw new UnsupportedOperationException("not supported type:" + type);
  }

  private static <T extends SpotifyItem> List<T> getSpotifyItems(Integer size, Class<T> type) {
    List<T> items = new ArrayList<>();
    IntStream.range(0, size).forEach(index -> items.add(index, getSpotifyItem(type)));
    return items;
  }

  public static List<SpotifyArtistItem> getArtistItems(Integer size) {
    return getSpotifyClientItems(size, SpotifyArtistItem.class);
  }

  public static List<SpotifyTrackItem> getTrackItems(Integer size) {
    return getSpotifyClientItems(size, SpotifyTrackItem.class);
  }

  public static List<SpotifyPlaylistItemTrack> getPlaylistItemTracks(Integer size) {
    return getSpotifyClientItems(size, SpotifyPlaylistItemTrack.class);
  }

  public static SpotifyPlaylistItemMusic getSpotifyPlaylistItemMusic(List<SpotifyPlaylistItemTrack> playlistItemTracks) {
    URL href;
    try {
      href = new URL("https://api.spotify.com");
    } catch (MalformedURLException e) {
      throw new RuntimeException("unable to create URL" + e.getMessage(), e);
    }
    Integer offset = 0;
    Integer total = 50;
    Integer limit = 50;
    return SpotifyPlaylistItemMusic.builder()
        .next(null)
        .href(href)
        .previous(null)
        .total(total)
        .offset(offset)
        .limit(limit)
        .playlistItemTracks(playlistItemTracks)
        .build();
  }

  private static <T> List<T> getSpotifyClientItems(Integer size, Class<T> type) {
    List<T> items = new ArrayList<>();
    IntStream.range(0, size).forEach(index -> items.add(index, getSpotifyClientItem(type)));
    return items;
  }

  private static <T> T getSpotifyClientItem(Class<T> type) {
    if (type.isAssignableFrom(SpotifyUserProfileItem.class)) return type.cast(getUserProfileItem());
    if (type.isAssignableFrom(SpotifyAlbumItem.class)) return type.cast(getAlbumItem());
    if (type.isAssignableFrom(SpotifyArtistItem.class)) return type.cast(getArtistItem());
    if (type.isAssignableFrom(SpotifyTrackItem.class)) return type.cast(getTrackItem());
    if (type.isAssignableFrom(SpotifyPlaylistItemDetails.class))
      return type.cast(getPlaylistItemDetails());
    if (type.isAssignableFrom(SpotifyPlaylistItem.class)) return type.cast(getPlaylistItem());
    if (type.isAssignableFrom(SpotifyPlaylistItemTrack.class))
      return type.cast(getSpotifyPlaylistItemTrack());
    throw new UnsupportedOperationException("not supported type:" + type);
  }

  private static String getRandomId() {
    return RandomStringUtils.randomAlphanumeric(22);
  }

  private static String getRandomSnapshotId() {
    return RandomStringUtils.randomAlphanumeric(57);
  }

  private static String getRandomName() {
    return RandomStringUtils.randomAlphabetic(5, 15);
  }

  private static String getRandomDescription() {
    return getRandomName() + " " + getRandomName() + " " + getRandomName();
  }

  private static String getRandomEmail() {
    return RandomStringUtils.randomAlphanumeric(5, 10) + "@mail.com";
  }

  private static Integer getRandomPopularity() {
    return current().nextInt(1, 101);
  }

  private static List<String> getRandomGenres() {
    List<String> genres = new ArrayList<>();
    int genresSize = current().nextInt(1, 5);
    IntStream.range(0, genresSize)
        .forEach(index -> genres.add(index, RandomStringUtils.randomAlphabetic(1, 15)));
    return genres;
  }

  public static Map<String, Object> getExplicitContent() {
    return Map.of("filter_enabled", false, "filter_locked", false);
  }

  private static Map<String, Object> getFollowers() {
    return Map.of("href", "", "total", 100000);
  }

  private static Map<String, Object> getExternalUrls(SpotifyItemType type, String id) {
    return Map.of("spotify", "https://open.spotify.com/" + type.getType() + "/" + id);
  }

  private static Map<String, Object> getExternalIds(SpotifyItemType type, String id) {
    return Map.of("spotify", "https://open.spotify.com/" + type.getType() + "/" + id);
  }

  private static Map<String, Object> getVideoThumbNail(String id) {
    return Map.of("video", "https://www.video.com/" + id);
  }

  private static List<Map<String, Object>> getRestrictions() {
    return List.of(Map.of("PG18", true, "PG21", false));
  }

  private static List<Map<String, Object>> getImages() {
    try {
      return List.of(
          Map.of("height", 640, "width", 640, "url", new URL("https://i.scdn.co/image/1")),
          Map.of("height", 320, "width", 320, "url", new URL("https://i.scdn.co/image/2")),
          Map.of("height", 160, "width", 160, "url", new URL("https://i.scdn.co/image/3")));
    } catch (MalformedURLException e) {
      throw new RuntimeException("unable to create URL: " + e.getMessage(), e);
    }
  }

  public static String getCountry() {
    return "US";
  }

  public static String getPrimaryColor() {
    return "Blue";
  }

  public static String getProduct() {
    return "premium";
  }

  public static String getAlbumType() {
    return "single";
  }

  public static String getAlbumGroup() {
    return "group";
  }

  public static String getReleaseDate() {
    return "2021-04-20";
  }

  public static String getPrecisionDate() {
    return "day";
  }

  public static List<String> getAvailableMarkets() {
    return List.of("US", "GB");
  }

  public static URL getHref(SpotifyItemType type, String id) {
    try {
      return new URL("https://api.spotify.com/v1/" + type + "s/" + id);

    } catch (MalformedURLException e) {
      throw new RuntimeException("unable to create URL: " + e.getMessage(), e);
    }
  }

  @Getter
  @AllArgsConstructor
  enum SpotifyItemType {
    USER("user"),
    ARTIST("artist"),

    TRACK("track"),
    ALBUM("album"),

    PLAYLIST("playlist");

    private final String type;

    public URI getUri(String id) {
      return URI.create("spotify:" + type + ":" + id);
    }

    @Override
    public String toString() {
      return this.type;
    }
  }
}
