package com.ksaraev.spotifyrun.utils;

import static com.ksaraev.spotifyrun.utils.SpotifyHelper.SpotifyItemType.*;
import static java.util.concurrent.ThreadLocalRandom.*;

import com.ksaraev.spotifyrun.client.dto.*;
import com.ksaraev.spotifyrun.spotify.model.SpotifyItem;
import com.ksaraev.spotifyrun.spotify.model.artist.SpotifyArtist;
import com.ksaraev.spotifyrun.spotify.model.artist.SpotifyArtistItem;
import com.ksaraev.spotifyrun.spotify.model.playlist.SpotifyPlaylist;
import com.ksaraev.spotifyrun.spotify.model.playlist.SpotifyPlaylistItem;
import com.ksaraev.spotifyrun.spotify.model.playlistdetails.SpotifyPlaylistDetails;
import com.ksaraev.spotifyrun.spotify.model.playlistdetails.SpotifyPlaylistItemDetails;
import com.ksaraev.spotifyrun.spotify.model.track.SpotifyTrack;
import com.ksaraev.spotifyrun.spotify.model.track.SpotifyTrackItem;
import com.ksaraev.spotifyrun.spotify.model.trackfeatures.SpotifyTrackFeatures;
import com.ksaraev.spotifyrun.spotify.model.trackfeatures.SpotifyTrackItemFeatures;
import com.ksaraev.spotifyrun.spotify.model.userprofile.SpotifyUserProfile;
import com.ksaraev.spotifyrun.spotify.model.userprofile.SpotifyUserProfileItem;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.RandomStringUtils;

public class SpotifyHelper {

  public static SpotifyUserProfileItem getUser() {
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
    SpotifyUserProfileItem user = getUser();
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

  public static SpotifyUserProfileDto getUserProfileItem() {
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
    return SpotifyUserProfileDto.builder()
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

  public static SpotifyTrackDto getTrackItem() {
    String id = getRandomId();
    String name = getRandomName();
    URI uri = TRACK.getUri(id);
    SpotifyAlbumDto albumItem = getAlbumItem();
    SpotifyAlbumDto sourceAlbumItem = getAlbumItem();
    URL previewURL = getHref(TRACK, id);
    List<SpotifyArtistDto> artistItems = getArtistItems(1);
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
    return SpotifyTrackDto.builder()
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

  public static SpotifyPlaylistDetailsDto getPlaylistItemDetails() {
    String name = getRandomName();
    String description = getRandomDescription();
    Boolean isPublic = true;
    Boolean isCollaborative = false;
    return SpotifyPlaylistDetailsDto.builder()
        .name(name)
        .description(description)
        .isPublic(isPublic)
        .isCollaborative(isCollaborative)
        .build();
  }

  public static SpotifyAlbumDto getAlbumItem() {
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
    List<SpotifyArtistDto> artistItems = getArtistItems(1);
    List<Map<String, Object>> restrictions = getRestrictions();
    Map<String, Object> externalUrls = getExternalUrls(ARTIST, id);
    List<Map<String, Object>> images = getImages();
    return SpotifyAlbumDto.builder()
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

  public static SpotifyArtistDto getArtistItem() {
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
    return SpotifyArtistDto.builder()
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

  public static SpotifyPlaylistDto getPlaylistItem() {
    String id = getRandomId();
    return getPlaylistItem(id);
  }

  public static SpotifyPlaylistDto getPlaylistItem(String id) {
    String name = getRandomName();
    URI uri = PLAYLIST.getUri(id);
    SpotifyUserProfileDto userProfileItem = getUserProfileItem();
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
    return SpotifyPlaylistDto.builder()
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
        .build();
  }

  public static SpotifyPlaylistTrackDto getSpotifyPlaylistItemTrack() {
    SpotifyTrackDto trackItem = getTrackItem();
    SpotifyUserProfileDto addedBy = getUserProfileItem();
    return getSpotifyPlaylistItemTrack(addedBy, trackItem);
  }

  public static SpotifyPlaylistTrackDto getSpotifyPlaylistItemTrack(
      SpotifyUserProfileDto addedBy, SpotifyTrackDto trackItem) {
    String addedAt = ZonedDateTime.now().toString();
    Boolean isLocal = false;
    String primaryColor = getPrimaryColor();
    Map<String, Object> videoThumbnail = getVideoThumbNail(trackItem.id());
    return SpotifyPlaylistTrackDto.builder()
        .trackItem(trackItem)
        .addedBy(addedBy)
        .addedAt(addedAt)
        .isLocal(isLocal)
        .primaryColor(primaryColor)
        .videoThumbnail(videoThumbnail)
        .build();
  }

  public static List<SpotifyPlaylistTrackDto> getSpotifyPlaylistItemTracks(
      SpotifyUserProfileDto addedBy, List<SpotifyTrackDto> trackItems) {
    return trackItems.stream()
        .filter(Objects::nonNull)
        .map(trackItem -> getSpotifyPlaylistItemTrack(addedBy, trackItem))
        .toList();
  }

  public static SpotifyPlaylistMusicDto getSpotifyPlaylistItemMusic(
      SpotifyUserProfileDto userProfileItem,
      List<SpotifyTrackDto> trackItems) {
    List<SpotifyPlaylistTrackDto> playlistItemTracks =
        getSpotifyPlaylistItemTracks(userProfileItem, trackItems);
    return SpotifyPlaylistMusicDto.builder()
        .playlistItemTracks(playlistItemTracks)
        .next(null)
        .previous(null)
        .href(null)
        .total(trackItems.size())
        .limit(100)
        .offset(0)
        .build();
  }

  public static SpotifyPlaylistDto updatePlaylist(
      SpotifyPlaylistDto playlistItem,
      List<SpotifyTrackDto> trackItems) {
    String id = playlistItem.id();
    String name = playlistItem.name();
    URI uri = playlistItem.uri();
    SpotifyUserProfileDto userProfileItem = playlistItem.userProfileItem();
    String snapshotId = getRandomSnapshotId();
    String description = playlistItem.description();
    String type = PLAYLIST.getType();
    Boolean isPublic = true;
    Boolean isCollaborative = false;
    String primaryColor = getPrimaryColor();
    Map<String, Object> followers = getFollowers();
    Map<String, Object> externalUrls = getExternalUrls(PLAYLIST, playlistItem.id());
    List<Map<String, Object>> images = getImages();
    URL href = getHref(PLAYLIST, playlistItem.id());
    SpotifyPlaylistMusicDto playlistItemMusic =
        getSpotifyPlaylistItemMusic(playlistItem.userProfileItem(), trackItems);
    return SpotifyPlaylistDto.builder()
        .id(id)
        .name(name)
        .uri(uri)
        .userProfileItem(userProfileItem)
        .playlistItemMusic(playlistItemMusic)
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
      List<SpotifyTrackDto> trackItems) throws Exception {
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

  public static GetRecommendationsResponse createGetRecommendationsResponse(
      List<SpotifyTrackDto> trackItems) {
    List<Map<String, Object>> seeds =
        List.of(
            Map.of(
                "initialPoolSize",
                428,
                "afterFilteringSize",
                238,
                "afterRelinkingSize",
                238,
                "id",
                "0000567890AaBbCcDdEeFfG",
                "type",
                "ARTIST",
                "href",
                "https://api.spotify.com/v1/artists/0000567890AaBbCcDdEeFfG"),
            Map.of(
                "initialPoolSize",
                425,
                "afterFilteringSize",
                222,
                "afterRelinkingSize",
                222,
                "id",
                "112233445AaBbCcDdEeFfG",
                "type",
                "TRACK",
                "href",
                "https://api.spotify.com/v1/tracks/1122AA4450011CcDdEeFfG"),
            Map.of(
                "initialPoolSize",
                160,
                "afterFilteringSize",
                58,
                "afterRelinkingSize",
                58,
                "id",
                "genre name",
                "type",
                "GENRE",
                "href",
                ""));
    return GetRecommendationsResponse.builder().trackItems(trackItems).seeds(seeds).build();
  }

  public static UpdateUpdateItemsResponse createAddItemsResponse() {
    String snapshotId = getRandomSnapshotId();
    return UpdateUpdateItemsResponse.builder().snapshotId(snapshotId).build();
  }

  public static List<SpotifyArtistItem> getArtists(Integer size) {
    return getSpotifyItems(size, SpotifyArtistItem.class);
  }

  public static List<SpotifyTrackItem> getTracks(Integer size) {
    return getSpotifyItems(size, SpotifyTrackItem.class);
  }

  private static <T extends SpotifyItem> T getSpotifyItem(Class<T> type) {
    if (type.isAssignableFrom(SpotifyUserProfileItem.class)) return type.cast(getUser());
    if (type.isAssignableFrom(SpotifyTrackItem.class)) return type.cast(getTrack());
    if (type.isAssignableFrom(SpotifyArtistItem.class)) return type.cast(getArtist());
    if (type.isAssignableFrom(SpotifyPlaylistItemDetails.class)) return type.cast(getPlaylistDetails());
    if (type.isAssignableFrom(SpotifyPlaylistItem.class)) return type.cast(getPlaylist());
    throw new UnsupportedOperationException("not supported type:" + type);
  }

  private static <T extends SpotifyItem> List<T> getSpotifyItems(Integer size, Class<T> type) {
    List<T> items = new ArrayList<>();
    IntStream.range(0, size).forEach(index -> items.add(index, getSpotifyItem(type)));
    return items;
  }

  public static List<SpotifyArtistDto> getArtistItems(Integer size) {
    return getSpotifyClientItems(size, SpotifyArtistDto.class);
  }

  public static List<SpotifyTrackDto> getTrackItems(Integer size) {
    return getSpotifyClientItems(size, SpotifyTrackDto.class);
  }

  private static <T> List<T> getSpotifyClientItems(Integer size, Class<T> type) {
    List<T> items = new ArrayList<>();
    IntStream.range(0, size).forEach(index -> items.add(index, getSpotifyClientItem(type)));
    return items;
  }

  private static <T> T getSpotifyClientItem(Class<T> type) {
    if (type.isAssignableFrom(SpotifyUserProfileDto.class)) return type.cast(getUserProfileItem());
    if (type.isAssignableFrom(SpotifyAlbumDto.class)) return type.cast(getAlbumItem());
    if (type.isAssignableFrom(SpotifyArtistDto.class)) return type.cast(getArtistItem());
    if (type.isAssignableFrom(SpotifyTrackDto.class)) return type.cast(getTrackItem());
    if (type.isAssignableFrom(SpotifyPlaylistDetailsDto.class))
      return type.cast(getPlaylistItemDetails());
    if (type.isAssignableFrom(SpotifyPlaylistDto.class)) return type.cast(getPlaylistItem());
    if (type.isAssignableFrom(SpotifyPlaylistTrackDto.class))
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
