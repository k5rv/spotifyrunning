package com.ksaraev.spotifyrun.mapping;

import com.ksaraev.spotifyrun.client.items.SpotifyTrackItem;
import com.ksaraev.spotifyrun.model.artist.Artist;
import com.ksaraev.spotifyrun.model.artist.ArtistMapperImpl;
import com.ksaraev.spotifyrun.model.track.Track;
import com.ksaraev.spotifyrun.model.track.TrackMapper;
import com.ksaraev.spotifyrun.model.track.TrackMapperImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static com.ksaraev.spotifyrun.utils.JsonHelper.jsonToObject;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TrackMapperImpl.class, ArtistMapperImpl.class})
class TrackMapperTest {

  @Autowired TrackMapper underTest;

  @Test
  void itShouldMapSpotifyTrackItemToTrack() {
    // Given
    String artistId = "5VnrVRYzaatWXs102ScGwN";
    String artistName = "Name";
    URI artistUri = URI.create("spotify:artist:5VnrVRYzaatWXs102ScGwN");
    Artist artist =
        Artist.builder().id(artistId).name(artistName).uri(artistUri).genres(null).build();
    String trackId = "5Ko5Jn0OG8IDFEHhAYsCnj";
    String trackName = "Name";
    URI trackUri = URI.create("spotify:track:5Ko5Jn0OG8IDFEHhAYsCnj");
    Integer trackPopularity = 32;
    Track track =
        Track.builder()
            .id(trackId)
            .name(trackName)
            .uri(trackUri)
            .popularity(trackPopularity)
            .artists(List.of(artist))
            .build();
    String spotifyTrackItemJson =
        """
         {
           "album":{
             "album_type":"ALBUM",
             "artists":[
               {
                 "external_urls":{
                   "spotify":"https://open.spotify.com/artist/5VnrVRYzaatWXs102ScGwN"
                 },
                 "href":"https://api.spotify.com/v1/artists/5VnrVRYzaatWXs102ScGwN",
                 "id":"5VnrVRYzaatWXs102ScGwN",
                 "name":"Artist Name",
                 "type":"artist",
                 "uri":"spotify:artist:5VnrVRYzaatWXs102ScGwN"
               }
              ],
             "available_markets":[
               "US",
               "GB"
             ],
             "external_urls":{
               "spotify":"https://open.spotify.com/album/0t58MAAaEcFOfp5aeljocU"
             },
             "href":"https://api.spotify.com/v1/albums/0t58MAAaEcFOfp5aeljocU",
             "id":"0t58MAAaEcFOfp5aeljocU",
             "images":[
               {
                 "height":640,
                 "url":"https://i.scdn.co/image/ab67626d000008514b8b7ed8a19cef47f4438913",
                 "width":640
               },
               {
                 "height":300,
                 "url":"https://i.scdn.co/image/ab67626d000008514b8b7ed8a19cef47f4438913",
                 "width":300
               },
               {
                 "height":64,
                 "url":"https://i.scdn.co/image/ab67626d000008514b8b7ed8a19cef47f4438913",
                 "width":64
               }
             ],
             "name":"Name",
             "release_date":"2001-06-11",
             "release_date_precision":"day",
             "total_tracks":10,
             "type":"album",
             "uri":"spotify:album:0t58MAAaEcFOfp5aeljocU"
           },
          "artists":[
             {
               "external_urls":{
                 "spotify":"https://open.spotify.com/artist/5VnrVRYzaatWXs102ScGwN"
               },
               "href":"https://api.spotify.com/v1/artists/5VnrVRYzaatWXs102ScGwN",
               "id":"%s",
               "name":"%s",
               "type":"artist",
               "uri":"%s"
             }
           ],
           "available_markets":[
             "US",
             "GB"
           ],
           "disc_number":1,
           "duration_ms":239493,
           "explicit":false,
           "external_ids":{
             "isrc":"CCEA32135502"
           },
           "external_urls":{
             "spotify":"https://open.spotify.com/track/5Ko5Jn0OG8IDFEHhAYsCnj"
           },
           "href":"https://api.spotify.com/v1/tracks/5Ko5Jn0OG8IDFEHhAYsCnj",
           "id":"%s",
           "is_local":false,
           "name":"%s",
           "popularity":%s,
           "preview_url":"https://p.scdn.co/mp3-preview/ab67626d000008514b8b7ed8a19cef47f4438913?cid=774b29d4f13844c495f206cafdad9c86",
           "track_number":9,
           "type":"track",
           "uri":"%s"
         }
         """
            .formatted(
                artistId, artistName, artistUri, trackId, trackName, trackPopularity, trackUri);

    // Then
    Assertions.assertThat(
            underTest.mapToTrack(jsonToObject(spotifyTrackItemJson, SpotifyTrackItem.class)))
        .isEqualTo(track)
        .hasOnlyFields("id", "name", "uri", "popularity", "artists");
  }

  @Test
  void itShouldMapSpotifyTrackItemsToTracks() {
    String artistId = "5VnrVRYzaatWXs102ScGwN";
    String artistName = "name artist";
    URI artistUri = URI.create("spotify:artist:5VnrVRYzaatWXs102ScGwN");

    Artist artist =
        Artist.builder().id(artistId).name(artistName).uri(artistUri).genres(null).build();

    String trackOneId = "5Ko5Jn0OG8IDFEHhAYsCnj";
    String trackOneName = "name one";
    URI trackOneUri = URI.create("spotify:track:5Ko5Jn0OG8IDFEHhAYsCnj");
    Integer trackOnePopularity = 32;

    Track trackOne =
        Track.builder()
            .id(trackOneId)
            .name(trackOneName)
            .uri(trackOneUri)
            .popularity(trackOnePopularity)
            .artists(List.of(artist))
            .build();

    String trackTwoId = "6Xo5Jn0OG8IDFEHhAYsCnj";
    String trackTwoName = "name two";
    URI trackTwoUri = URI.create("spotify:track:6Xo5Jn0OG8IDFEHhAYsCnj");
    Integer trackTwoPopularity = 99;

    Track trackTwo =
        Track.builder()
            .id(trackTwoId)
            .name(trackTwoName)
            .uri(trackTwoUri)
            .popularity(trackTwoPopularity)
            .artists(List.of(artist))
            .build();

    String spotifyTrackItemJson =
        """
         {
           "album":{
             "album_type":"ALBUM",
             "artists":[
               {
                 "external_urls":{
                   "spotify":"https://open.spotify.com/artist/5VnrVRYzaatWXs102ScGwN"
                 },
                 "href":"https://api.spotify.com/v1/artists/5VnrVRYzaatWXs102ScGwN",
                 "id":"5VnrVRYzaatWXs102ScGwN",
                 "name":"Artist Name",
                 "type":"artist",
                 "uri":"spotify:artist:5VnrVRYzaatWXs102ScGwN"
               }
              ],
             "available_markets":[
               "US",
               "GB"
             ],
             "external_urls":{
               "spotify":"https://open.spotify.com/album/0t58MAAaEcFOfp5aeljocU"
             },
             "href":"https://api.spotify.com/v1/albums/0t58MAAaEcFOfp5aeljocU",
             "id":"0t58MAAaEcFOfp5aeljocU",
             "images":[
               {
                 "height":640,
                 "url":"https://i.scdn.co/image/ab67626d000008514b8b7ed8a19cef47f4438913",
                 "width":640
               },
               {
                 "height":300,
                 "url":"https://i.scdn.co/image/ab67626d000008514b8b7ed8a19cef47f4438913",
                 "width":300
               },
               {
                 "height":64,
                 "url":"https://i.scdn.co/image/ab67626d000008514b8b7ed8a19cef47f4438913",
                 "width":64
               }
             ],
             "name":"Name",
             "release_date":"2001-06-11",
             "release_date_precision":"day",
             "total_tracks":10,
             "type":"album",
             "uri":"spotify:album:0t58MAAaEcFOfp5aeljocU"
           },
          "artists":[
             {
               "external_urls":{
                 "spotify":"https://open.spotify.com/artist/5VnrVRYzaatWXs102ScGwN"
               },
               "href":"https://api.spotify.com/v1/artists/5VnrVRYzaatWXs102ScGwN",
               "id":"%s",
               "name":"%s",
               "type":"artist",
               "uri":"%s"
             }
           ],
           "available_markets":[
             "US",
             "GB"
           ],
           "disc_number":1,
           "duration_ms":239493,
           "explicit":false,
           "external_ids":{
             "isrc":"CCEA32135502"
           },
           "external_urls":{
             "spotify":"https://open.spotify.com/track/5Ko5Jn0OG8IDFEHhAYsCnj"
           },
           "href":"https://api.spotify.com/v1/tracks/5Ko5Jn0OG8IDFEHhAYsCnj",
           "id":"%s",
           "is_local":false,
           "name":"%s",
           "popularity":%s,
           "preview_url":"https://p.scdn.co/mp3-preview/ab67626d000008514b8b7ed8a19cef47f4438913?cid=774b29d4f13844c495f206cafdad9c86",
           "track_number":9,
           "type":"track",
           "uri":"%s"
         }
         """;

    SpotifyTrackItem spotifyTrackItemOne =
        jsonToObject(
            spotifyTrackItemJson.formatted(
                artistId,
                artistName,
                artistUri,
                trackOneId,
                trackOneName,
                trackOnePopularity,
                trackOneUri),
            SpotifyTrackItem.class);

    SpotifyTrackItem spotifyTrackItemTwo =
        jsonToObject(
            spotifyTrackItemJson.formatted(
                artistId,
                artistName,
                artistUri,
                trackTwoId,
                trackTwoName,
                trackTwoPopularity,
                trackTwoUri),
            SpotifyTrackItem.class);

    Assertions.assertThat(
            underTest.mapItemsToTracks(Arrays.asList(spotifyTrackItemOne, spotifyTrackItemTwo)))
        .containsExactly(trackOne, trackTwo);
  }

  @Test
  void itShouldReturnEmptyListWhenSourceIsNull() {
    Assertions.assertThat(underTest.mapItemsToTracks(null)).isEmpty();
  }
}
