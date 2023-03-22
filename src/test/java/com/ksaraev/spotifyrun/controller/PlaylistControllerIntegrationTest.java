package com.ksaraev.spotifyrun.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.ksaraev.spotifyrun.utils.JsonHelper.*;
import static com.ksaraev.spotifyrun.utils.SpotifyHelper.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ksaraev.spotifyrun.app.playlist.PlaylistController;
import com.ksaraev.spotifyrun.client.api.AddItemsResponse;
import com.ksaraev.spotifyrun.client.api.GetRecommendationsResponse;
import com.ksaraev.spotifyrun.client.api.GetUserTopTracksResponse;
import com.ksaraev.spotifyrun.client.api.SpotifyPlaylistDto;
import com.ksaraev.spotifyrun.client.api.SpotifyTrackDto;
import com.ksaraev.spotifyrun.client.api.SpotifyUserProfileDto;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles(value = "test")
@AutoConfigureWireMock(port = 0)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class PlaylistControllerIntegrationTest {

  @Autowired PlaylistController underTest;

  @Autowired private MockMvc mockMvc;

  @Test
  void itShouldCreatePlaylistSuccessfully() throws Exception {
    // Given
    SpotifyUserProfileDto userProfileItem = getUserProfileItem();
    String userId = userProfileItem.id();

    stubFor(get(urlEqualTo("/v1/me")).willReturn(jsonResponse(objectToJson(userProfileItem), 200)));

    List<SpotifyTrackDto> topTrackItems = getTrackItems(60);
    GetUserTopTracksResponse getUserTopTracksResponse =
        createGetUserTopTracksResponse(topTrackItems);

    stubFor(
        get(urlPathEqualTo("/v1/me/top/tracks"))
            .willReturn(jsonResponse(objectToJson(getUserTopTracksResponse), 200)));

    List<SpotifyTrackDto> musicRecommendations = getTrackItems(60);
    GetRecommendationsResponse getRecommendationsResponse =
        createGetRecommendationsResponse(musicRecommendations);

    stubFor(
        get(urlPathEqualTo("/v1/recommendations"))
            .inScenario("recommendations")
            .willReturn(jsonResponse(objectToJson(getRecommendationsResponse), 200)));

    SpotifyPlaylistDto emptyPlaylist = getPlaylistItem(userId);
    String playlistId = emptyPlaylist.id();

    stubFor(
        post(urlEqualTo("/v1/users/" + userId + "/playlists"))
            .willReturn(jsonResponse(objectToJson(emptyPlaylist), 200)));

    AddItemsResponse addItemsResponse = createAddItemsResponse();

    stubFor(
        post(urlEqualTo("/v1/playlists/" + playlistId + "/tracks"))
            .willReturn(jsonResponse(objectToJson(addItemsResponse), 200)));

    SpotifyPlaylistDto playlist = updatePlaylist(emptyPlaylist, musicRecommendations);

    stubFor(
        get(urlEqualTo("/v1/playlists/" + playlistId))
            .willReturn(jsonResponse(objectToJson(playlist), 200)));

    // When
    ResultActions createPlaylistResultActions =
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/playlists")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()));

    // Then
    createPlaylistResultActions
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(playlist.id()))
        .andExpect(jsonPath("$.snapshotId").value(playlist.snapshotId()))
        .andDo(print());
  }
}
