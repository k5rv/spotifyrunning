package com.ksaraev.spotifyrun.service.toptracks;

import com.ksaraev.spotifyrun.client.items.SpotifyTrackItem;
import com.ksaraev.spotifyrun.model.artist.Artist;
import com.ksaraev.spotifyrun.model.artist.ArtistMapperImpl;
import com.ksaraev.spotifyrun.model.spotify.SpotifyArtist;
import com.ksaraev.spotifyrun.model.spotify.SpotifyTrack;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.ksaraev.spotifyrun.utils.JsonHelper.jsonToObject;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {TrackMapperImpl.class, ArtistMapperImpl.class})
class TracksMapperTest {

  @Autowired TrackMapper underTest;

  @Test
  void itShouldMapSpotifyTrackItemToTrack() {
    // Given
    String artistId = "5VnrVRYzaatWXs102ScGwN";
    String artistName = "Artist Name";
    URI artistUri = URI.create("spotify:artist:5VnrVRYzaatWXs102ScGwN");

    String trackId = "5Ko5Jn0OG8IDFEHhAYsCnj";
    String trackName = "Track Name";
    URI trackUri = URI.create("spotify:track:5Ko5Jn0OG8IDFEHhAYsCnj");
    int trackPopularity = 32;

    SpotifyTrack track =
        new Track(
            trackId,
            trackName,
            trackUri,
            trackPopularity,
            List.of(new Artist(artistId, artistName, artistUri, null)));

    String json =
        "    {\n"
            + "      \"album\":{\n"
            + "        \"album_type\":\"ALBUM\",\n"
            + "        \"artists\":[\n"
            + "          {\n"
            + "            \"external_urls\":{\n"
            + "              \"spotify\":\"https://open.spotify.com/artist/5VnrVRYzaatWXs102ScGwN\"\n"
            + "            },\n"
            + "            \"href\":\"https://api.spotify.com/v1/artists/5VnrVRYzaatWXs102ScGwN\",\n"
            + "            \"id\":\""
            + artistId
            + "\",\n"
            + "            \"name\":\""
            + artistName
            + "\",\n"
            + "            \"type\":\"artist\",\n"
            + "            \"uri\":\""
            + artistUri
            + "\"\n"
            + "          }\n"
            + "        ],\n"
            + "        \"available_markets\":[\n"
            + "          \"US\",\n"
            + "          \"GB\"\n"
            + "        ],\n"
            + "        \"external_urls\":{\n"
            + "          \"spotify\":\"https://open.spotify.com/album/0t58MAAaEcFOfp5aeljocU\"\n"
            + "        },\n"
            + "        \"href\":\"https://api.spotify.com/v1/albums/0t58MAAaEcFOfp5aeljocU\",\n"
            + "        \"id\":\"0t58MAAaEcFOfp5aeljocU\",\n"
            + "        \"images\":[\n"
            + "          {\n"
            + "            \"height\":640,\n"
            + "            \"url\":\"https://i.scdn.co/image/ab67626d000008514b8b7ed8a19cef47f4438913\",\n"
            + "            \"width\":640\n"
            + "          },\n"
            + "          {\n"
            + "            \"height\":300,\n"
            + "            \"url\":\"https://i.scdn.co/image/ab67626d000008514b8b7ed8a19cef47f4438913\",\n"
            + "            \"width\":300\n"
            + "          },\n"
            + "          {\n"
            + "            \"height\":64,\n"
            + "            \"url\":\"https://i.scdn.co/image/ab67626d000008514b8b7ed8a19cef47f4438913\",\n"
            + "            \"width\":64\n"
            + "          }\n"
            + "        ],\n"
            + "        \"name\":\"Name\",\n"
            + "        \"release_date\":\"2001-06-11\",\n"
            + "        \"release_date_precision\":\"day\",\n"
            + "        \"total_tracks\":10,\n"
            + "        \"type\":\"album\",\n"
            + "        \"uri\":\"spotify:album:0t58MAAaEcFOfp5aeljocU\"\n"
            + "      },\n"
            + "      \"artists\":[\n"
            + "        {\n"
            + "          \"external_urls\":{\n"
            + "            \"spotify\":\"https://open.spotify.com/artist/5VnrVRYzaatWXs102ScGwN\"\n"
            + "          },\n"
            + "          \"href\":\"https://api.spotify.com/v1/artists/5VnrVRYzaatWXs102ScGwN\",\n"
            + "          \"id\":\""
            + artistId
            + "\",\n"
            + "          \"name\":\""
            + artistName
            + "\",\n"
            + "          \"type\":\"artist\",\n"
            + "          \"uri\":\""
            + artistUri
            + "\"\n"
            + "        }\n"
            + "      ],\n"
            + "      \"available_markets\":[\n"
            + "        \"US\",\n"
            + "        \"GB\"\n"
            + "      ],\n"
            + "      \"disc_number\":1,\n"
            + "      \"duration_ms\":239493,\n"
            + "      \"explicit\":false,\n"
            + "      \"external_ids\":{\n"
            + "        \"isrc\":\"UKEX32135502\"\n"
            + "      },\n"
            + "      \"external_urls\":{\n"
            + "        \"spotify\":\"https://open.spotify.com/track/5Ko5Jn0OG8IDFEHhAYsCnj\"\n"
            + "      },\n"
            + "      \"href\":\"https://api.spotify.com/v1/tracks/5Ko5Jn0OG8IDFEHhAYsCnj\",\n"
            + "      \"id\":\""
            + trackId
            + "\",\n"
            + "      \"is_local\":false,\n"
            + "      \"name\":\""
            + trackName
            + "\",\n"
            + "      \"popularity\":"
            + trackPopularity
            + ",\n"
            + "      \"preview_url\":\"https://p.scdn.co/mp3-preview/ab67626d000008514b8b7ed8a19cef47f4438913?cid=774b29d4f13844c495f206cafdad9c86\",\n"
            + "      \"track_number\":9,\n"
            + "      \"type\":\"track\",\n"
            + "      \"uri\":\""
            + trackUri
            + "\"\n"
            + "    }\n";

    // When and Then
    Assertions.assertThat(underTest.mapToTrack(jsonToObject(json, SpotifyTrackItem.class)))
        .isEqualTo(track)
        .hasOnlyFields("id", "name", "uri", "popularity", "artists");
  }

  @Test
  void itShouldMapSpotifyTrackItemsToTracks() {
    // Given
    String artistId = "5VnrVRYzaatWXs102ScGwN";
    String artistName = "Artist Name";
    URI artistUri = URI.create("spotify:artist:5VnrVRYzaatWXs102ScGwN");
    SpotifyArtist artist = new Artist(artistId, artistName, artistUri, null);

    String firstTrackId = "5Ko5Jn0OG8IDFEHhAYsCnj";
    String firstTrackName = "Track Name";
    URI firstTrackUri = URI.create("spotify:track:5Ko5Jn0OG8IDFEHhAYsCnj");
    int firstTrackPopularity = 32;
    SpotifyTrack firstTrack =
        new Track(
            firstTrackId, firstTrackName, firstTrackUri, firstTrackPopularity, List.of(artist));

    String secondTrackId = "6NnrVRYzaDtWXs102ScGwN";
    String secondTrackName = "Second Track Name";
    URI secondTrackUri = URI.create("spotify:track:6NnrVRYzaDtWXs102ScGwN");
    int secondTrackPopularity = 100;
    SpotifyTrack secondTrack =
        new Track(
            secondTrackId, secondTrackName, secondTrackUri, secondTrackPopularity, List.of(artist));

    String json =
        "    {\n"
            + "      \"album\":{\n"
            + "        \"album_type\":\"ALBUM\",\n"
            + "        \"artists\":[\n"
            + "          {\n"
            + "            \"external_urls\":{\n"
            + "              \"spotify\":\"https://open.spotify.com/artist/5VnrVRYzaatWXs102ScGwN\"\n"
            + "            },\n"
            + "            \"href\":\"https://api.spotify.com/v1/artists/5VnrVRYzaatWXs102ScGwN\",\n"
            + "            \"id\":\""
            + artistId
            + "\",\n"
            + "            \"name\":\""
            + artistName
            + "\",\n"
            + "            \"type\":\"artist\",\n"
            + "            \"uri\":\""
            + artistUri
            + "\"\n"
            + "          }\n"
            + "        ],\n"
            + "        \"available_markets\":[\n"
            + "          \"US\",\n"
            + "          \"GB\"\n"
            + "        ],\n"
            + "        \"external_urls\":{\n"
            + "          \"spotify\":\"https://open.spotify.com/album/0t58MAAaEcFOfp5aeljocU\"\n"
            + "        },\n"
            + "        \"href\":\"https://api.spotify.com/v1/albums/0t58MAAaEcFOfp5aeljocU\",\n"
            + "        \"id\":\"0t58MAAaEcFOfp5aeljocU\",\n"
            + "        \"images\":[\n"
            + "          {\n"
            + "            \"height\":640,\n"
            + "            \"url\":\"https://i.scdn.co/image/ab67626d000008514b8b7ed8a19cef47f4438913\",\n"
            + "            \"width\":640\n"
            + "          },\n"
            + "          {\n"
            + "            \"height\":300,\n"
            + "            \"url\":\"https://i.scdn.co/image/ab67626d000008514b8b7ed8a19cef47f4438913\",\n"
            + "            \"width\":300\n"
            + "          },\n"
            + "          {\n"
            + "            \"height\":64,\n"
            + "            \"url\":\"https://i.scdn.co/image/ab67626d000008514b8b7ed8a19cef47f4438913\",\n"
            + "            \"width\":64\n"
            + "          }\n"
            + "        ],\n"
            + "        \"name\":\"Name\",\n"
            + "        \"release_date\":\"2001-06-11\",\n"
            + "        \"release_date_precision\":\"day\",\n"
            + "        \"total_tracks\":10,\n"
            + "        \"type\":\"album\",\n"
            + "        \"uri\":\"spotify:album:0t58MAAaEcFOfp5aeljocU\"\n"
            + "      },\n"
            + "      \"artists\":[\n"
            + "        {\n"
            + "          \"external_urls\":{\n"
            + "            \"spotify\":\"https://open.spotify.com/artist/5VnrVRYzaatWXs102ScGwN\"\n"
            + "          },\n"
            + "          \"href\":\"https://api.spotify.com/v1/artists/5VnrVRYzaatWXs102ScGwN\",\n"
            + "          \"id\":\""
            + artistId
            + "\",\n"
            + "          \"name\":\""
            + artistName
            + "\",\n"
            + "          \"type\":\"artist\",\n"
            + "          \"uri\":\""
            + artistUri
            + "\"\n"
            + "        }\n"
            + "      ],\n"
            + "      \"available_markets\":[\n"
            + "        \"US\",\n"
            + "        \"GB\"\n"
            + "      ],\n"
            + "      \"disc_number\":1,\n"
            + "      \"duration_ms\":239493,\n"
            + "      \"explicit\":false,\n"
            + "      \"external_ids\":{\n"
            + "        \"isrc\":\"UKEX32135502\"\n"
            + "      },\n"
            + "      \"external_urls\":{\n"
            + "        \"spotify\":\"https://open.spotify.com/track/5Ko5Jn0OG8IDFEHhAYsCnj\"\n"
            + "      },\n"
            + "      \"href\":\"https://api.spotify.com/v1/tracks/5Ko5Jn0OG8IDFEHhAYsCnj\",\n"
            + "      \"id\":\""
            + firstTrackId
            + "\",\n"
            + "      \"is_local\":false,\n"
            + "      \"name\":\""
            + firstTrackName
            + "\",\n"
            + "      \"popularity\":"
            + firstTrackPopularity
            + ",\n"
            + "      \"preview_url\":\"https://p.scdn.co/mp3-preview/ab67626d000008514b8b7ed8a19cef47f4438913?cid=774b29d4f13844c495f206cafdad9c86\",\n"
            + "      \"track_number\":9,\n"
            + "      \"type\":\"track\",\n"
            + "      \"uri\":\""
            + firstTrackUri
            + "\"\n"
            + "    }\n";

    List<SpotifyTrackItem> trackItems =
        new ArrayList<>(
            List.of(Objects.requireNonNull(jsonToObject(json, SpotifyTrackItem.class))));

    json =
        json.replace(firstTrackId, secondTrackId)
            .replace(firstTrackName, secondTrackName)
            .replace(String.valueOf(firstTrackUri), String.valueOf(secondTrackUri))
            .replace(String.valueOf(firstTrackPopularity), String.valueOf(secondTrackPopularity));

    trackItems.add(jsonToObject(json, SpotifyTrackItem.class));

    // When and Then
    Assertions.assertThat(underTest.mapItemsToTracks(trackItems))
        .containsExactly(firstTrack, secondTrack);
  }

  @Test
  void itShouldReturnEmptyListWhenSourceIsNull() {
    // Given When Then
    List<SpotifyTrackItem> trackItems = null;
    Assertions.assertThat(underTest.mapItemsToTracks(trackItems)).isEmpty();
  }
}
