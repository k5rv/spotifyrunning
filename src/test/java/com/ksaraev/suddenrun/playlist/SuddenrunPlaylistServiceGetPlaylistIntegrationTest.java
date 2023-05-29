package com.ksaraev.suddenrun.playlist;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.ksaraev.spotify.client.dto.*;
import com.ksaraev.spotify.model.playlist.SpotifyPlaylistItemConfig;
import com.ksaraev.spotify.model.playlistdetails.SpotifyPlaylistItemDetails;
import com.ksaraev.suddenrun.exception.SuddenrunError;
import com.ksaraev.suddenrun.track.AppTrack;
import com.ksaraev.suddenrun.track.SuddenrunTrack;
import com.ksaraev.suddenrun.user.AppUserService;
import com.ksaraev.suddenrun.user.SuddenrunUser;
import com.ksaraev.suddenrun.user.SuddenrunUserRepository;
import com.ksaraev.utils.helpers.JsonHelper;
import com.ksaraev.utils.helpers.SpotifyClientHelper;
import com.ksaraev.utils.helpers.SpotifyResourceHelper;
import com.ksaraev.utils.helpers.SuddenrunHelper;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ActiveProfiles(value = "test")
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SuddenrunPlaylistServiceGetPlaylistIntegrationTest {

  private static final String SUDDENRUN_API_V1_PLAYLISTS = "/api/v1/playlists";

  private static final String SPOTIFY_API_V1_ME = "/v1/me";

  private static final String SPOTIFY_API_V1_PLAYLISTS_PLAYLIST_ID = "/v1/playlists/%s";

  private static final String SPOTIFY_API_V1_USERS_USER_ID_PLAYLISTS = "/v1/users/%s/playlists";
  @Autowired AppUserService appUserService;
  @Autowired SuddenrunUserRepository userRepository;
  @Autowired SuddenrunPlaylistRepository playlistRepository;
  @Autowired private SpotifyPlaylistItemConfig spotifyPlaylistConfig;
  @Autowired private MockMvc mockMvc;

  @Test
  void itShouldGetPlaylist() throws Exception {
    // Given
    SpotifyUserProfileDto spotifyUserDto = SpotifyClientHelper.getUserProfileDto();
    String spotifyUserId = spotifyUserDto.id();
    String spotifyUserName = spotifyUserDto.displayName();

    SuddenrunUser appUser = SuddenrunUser.builder().id(spotifyUserId).name(spotifyUserName).build();

    SpotifyPlaylistItemDetails spotifyPlaylistDetails = spotifyPlaylistConfig.getDetails();
    String playlistName = spotifyPlaylistDetails.getName();
    String playlistDescription = spotifyPlaylistDetails.getDescription();
    boolean playlistIsPublic = spotifyPlaylistDetails.getIsPublic();

    List<SpotifyTrackDto> spotifyPlaylistTracks = SpotifyClientHelper.getTrackDtos(10);

    String playlistId = SpotifyResourceHelper.getRandomId();

    SpotifyPlaylistDto spotifyPlaylistDto =
        SpotifyClientHelper.getPlaylistDto(
            spotifyUserDto,
            playlistId,
            playlistName,
            playlistDescription,
            playlistIsPublic,
            spotifyPlaylistTracks);

    SuddenrunPlaylist playlist = SuddenrunHelper.getSuddenrunPlaylist(appUser);
    playlist.setSnapshotId(spotifyPlaylistDto.snapshotId());
    playlist.setId(spotifyPlaylistDto.id());
    List<AppTrack> appPlaylistTracks =
        spotifyPlaylistTracks.stream()
            .map(
                spotifyTrack ->
                    SuddenrunTrack.builder()
                        .id(spotifyTrack.id())
                        .name(spotifyTrack.name())
                        .build())
            .map(AppTrack.class::cast)
            .toList();
    playlist.setTracks(appPlaylistTracks);
    appUser.setPlaylists(List.of(playlist));
    userRepository.save(appUser);
    playlistRepository.save(playlist);

    WireMock.stubFor(
        WireMock.get(WireMock.urlEqualTo(SPOTIFY_API_V1_ME))
            .willReturn(
                WireMock.jsonResponse(
                    JsonHelper.objectToJson(spotifyUserDto), HttpStatus.OK.value())));

    GetUserPlaylistsResponse getUserPlaylistsResponse =
        SpotifyClientHelper.createGetUserPlaylistResponse(List.of(spotifyPlaylistDto));

    WireMock.stubFor(
        WireMock.get(
                WireMock.urlEqualTo(
                    SPOTIFY_API_V1_USERS_USER_ID_PLAYLISTS.formatted(spotifyUserId)))
            .willReturn(
                WireMock.jsonResponse(
                    JsonHelper.objectToJson(getUserPlaylistsResponse), HttpStatus.OK.value())));

    WireMock.stubFor(
        WireMock.get(
                WireMock.urlEqualTo(SPOTIFY_API_V1_PLAYLISTS_PLAYLIST_ID.formatted(playlistId)))
            .inScenario("Get created playlist")
            .willSetStateTo("Got tracks")
            .willReturn(
                WireMock.jsonResponse(
                    JsonHelper.objectToJson(spotifyPlaylistDto), HttpStatus.OK.value())));

    // When
    ResultActions getPlaylistResultActions =
        mockMvc.perform(
            MockMvcRequestBuilders.get(SUDDENRUN_API_V1_PLAYLISTS)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf()));

    // Then
    MvcResult result =
        getPlaylistResultActions.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

    AppPlaylist appPlaylist =
        JsonHelper.jsonToAppPlaylist(result.getResponse().getContentAsString());

    Assertions.assertThat(appPlaylist.getId()).isEqualTo(playlistId);
    Assertions.assertThat(appPlaylist.getSnapshotId()).isEqualTo(spotifyPlaylistDto.snapshotId());
    Assertions.assertThat(appPlaylist.getTracks()).containsAll(appPlaylistTracks);
  }

  @Test
  void itShouldReturnHttp404IfPlaylistDoesNotExist() throws Exception {
    // Given
    SpotifyUserProfileDto spotifyUserDto = SpotifyClientHelper.getUserProfileDto();
    String spotifyUserId = spotifyUserDto.id();
    String spotifyUserName = spotifyUserDto.displayName();

    SuddenrunUser appUser = SuddenrunUser.builder().id(spotifyUserId).name(spotifyUserName).build();
    userRepository.save(appUser);

    WireMock.stubFor(
        WireMock.get(WireMock.urlEqualTo(SPOTIFY_API_V1_ME))
            .willReturn(
                WireMock.jsonResponse(
                    JsonHelper.objectToJson(spotifyUserDto), HttpStatus.OK.value())));

    // When
    ResultActions getPlaylistResultActions =
        mockMvc.perform(
            MockMvcRequestBuilders.get(SUDDENRUN_API_V1_PLAYLISTS)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf()));

    // Then
    MvcResult result =
        getPlaylistResultActions.andExpect(MockMvcResultMatchers.status().isNotFound()).andReturn();

    SuddenrunError error =
        JsonHelper.jsonToObject(result.getResponse().getContentAsString(), SuddenrunError.class);
    Assertions.assertThat(error).isNotNull();
    Assertions.assertThat(error.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
    Assertions.assertThat(error.message()).contains(spotifyUserId);
  }

  @Test
  void itShouldReturnAuthenticationErrorWhenSpotifyAuthorizationFailed() throws Exception {
    // Given
    WireMock.stubFor(
        WireMock.get(WireMock.urlEqualTo(SPOTIFY_API_V1_ME))
            .willReturn(
                WireMock.jsonResponse(HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.value())));

    // When
    ResultActions createPlaylistResultActions =
        mockMvc.perform(
            MockMvcRequestBuilders.get(SUDDENRUN_API_V1_PLAYLISTS)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf()));

    // Then
    MvcResult result =
        createPlaylistResultActions
            .andExpect(MockMvcResultMatchers.status().isUnauthorized())
            .andReturn();

    SuddenrunError error =
        JsonHelper.jsonToObject(result.getResponse().getContentAsString(), SuddenrunError.class);
    Assertions.assertThat(error).isNotNull();
    Assertions.assertThat(error.status()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
  }
}
