package com.suddenrun.utils;

import static com.suddenrun.utils.SpotifyResourceHelper.*;

import com.suddenrun.spotify.client.dto.*;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

public class SpotifyClientHelper {

  public static SpotifyUserProfileDto getUserProfileDto() {
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

  public static SpotifyTrackDto getTrackDto() {
    String id = getRandomId();
    String name = getRandomName();
    URI uri = TRACK.getUri(id);
    SpotifyAlbumDto albumItem = getAlbumDto();
    SpotifyAlbumDto sourceAlbumItem = getAlbumDto();
    URL previewURL = getHref(TRACK, id);
    List<SpotifyArtistDto> artistItems = getArtistDtos(1);
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

  public static SpotifyPlaylistDetailsDto getPlaylistDetailsDto() {
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

  public static SpotifyAlbumDto getAlbumDto() {
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
    List<SpotifyArtistDto> artistItems = getArtistDtos(1);
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

  public static SpotifyArtistDto getArtistDto() {
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

  public static SpotifyPlaylistDto getPlaylistDto() {
    String id = getRandomId();
    return getPlaylistDto(id);
  }

  public static SpotifyPlaylistDto getPlaylistDto(String id) {
    String name = getRandomName();
    URI uri = PLAYLIST.getUri(id);
    SpotifyUserProfileDto userProfileItem = getUserProfileDto();
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

  public static SpotifyPlaylistTrackDto getSpotifyPlaylistTrackDto() {
    SpotifyTrackDto trackItem = getTrackDto();
    SpotifyUserProfileDto addedBy = getUserProfileDto();
    return getSpotifyPlaylistTrackDto(addedBy, trackItem);
  }

  public static SpotifyPlaylistTrackDto getSpotifyPlaylistTrackDto(
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

  public static List<SpotifyPlaylistTrackDto> getSpotifyPlaylistTrackDtos(
      SpotifyUserProfileDto addedBy, List<SpotifyTrackDto> trackDtos) {
    return trackDtos.stream()
        .filter(Objects::nonNull)
        .map(trackItem -> getSpotifyPlaylistTrackDto(addedBy, trackItem))
        .toList();
  }

  public static SpotifyPlaylistMusicDto getSpotifyPlaylistMusicDto(
      SpotifyUserProfileDto userProfileItem, List<SpotifyTrackDto> trackItems) {
    List<SpotifyPlaylistTrackDto> playlistItemTracks =
        getSpotifyPlaylistTrackDtos(userProfileItem, trackItems);
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

  public static SpotifyPlaylistDto updatePlaylistDto(
      SpotifyPlaylistDto playlistItem, List<SpotifyTrackDto> trackItems) {
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
        getSpotifyPlaylistMusicDto(playlistItem.userProfileItem(), trackItems);
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

  public static List<SpotifyArtistDto> getArtistDtos(Integer size) {
    return getSpotifyClientDtos(size, SpotifyArtistDto.class);
  }

  public static List<SpotifyTrackDto> getTrackDtos(Integer size) {
    return getSpotifyClientDtos(size, SpotifyTrackDto.class);
  }

  private static <T> List<T> getSpotifyClientDtos(Integer size, Class<T> type) {
    List<T> items = new ArrayList<>();
    IntStream.range(0, size).forEach(index -> items.add(index, getSpotifyClientDto(type)));
    return items;
  }

  private static <T> T getSpotifyClientDto(Class<T> type) {
    if (type.isAssignableFrom(SpotifyUserProfileDto.class)) return type.cast(getUserProfileDto());
    if (type.isAssignableFrom(SpotifyAlbumDto.class)) return type.cast(getAlbumDto());
    if (type.isAssignableFrom(SpotifyArtistDto.class)) return type.cast(getArtistDto());
    if (type.isAssignableFrom(SpotifyTrackDto.class)) return type.cast(getTrackDto());
    if (type.isAssignableFrom(SpotifyPlaylistDetailsDto.class))
      return type.cast(getPlaylistDetailsDto());
    if (type.isAssignableFrom(SpotifyPlaylistDto.class)) return type.cast(getPlaylistDto());
    if (type.isAssignableFrom(SpotifyPlaylistTrackDto.class))
      return type.cast(getSpotifyPlaylistTrackDto());
    throw new UnsupportedOperationException("not supported type:" + type);
  }
}
