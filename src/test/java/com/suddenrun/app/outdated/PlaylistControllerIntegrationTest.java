package com.suddenrun.app.outdated;

//@AutoConfigureMockMvc(addFilters = false)
//@ActiveProfiles(value = "test")
//@AutoConfigureWireMock(port = 0)
//@SpringBootTest(webEnvironment = RANDOM_PORT)
class PlaylistControllerIntegrationTest {
/*
  @Autowired
  PlaylistController underTest;

  @Autowired private MockMvc mockMvc;

  @Test
  void itShouldCreatePlaylistSuccessfully() throws Exception {
    // Given
    SpotifyUserProfileDto userProfileItem = SpotifyClientHelper.getUserProfileDto();
    String userId = userProfileItem.id();

    stubFor(get(urlEqualTo("/v1/me")).willReturn(WireMock.jsonResponse(JsonHelper.objectToJson(userProfileItem), 200)));

    List<SpotifyTrackDto> topTrackItems = SpotifyClientHelper.getTrackDtos(60);
    GetUserTopTracksResponse getUserTopTracksResponse =
        SpotifyClientHelper.createGetUserTopTracksResponse(topTrackItems);

    stubFor(
        get(urlPathEqualTo("/v1/me/top/tracks"))
            .willReturn(WireMock.jsonResponse(JsonHelper.objectToJson(getUserTopTracksResponse), 200)));

    List<SpotifyTrackDto> musicRecommendations = SpotifyClientHelper.getTrackDtos(60);
    GetRecommendationsResponse getRecommendationsResponse =
        SpotifyClientHelper.createGetRecommendationsResponse(musicRecommendations);

    stubFor(
        get(urlPathEqualTo("/v1/recommendations"))
            .inScenario("recommendations")
            .willReturn(WireMock.jsonResponse(JsonHelper.objectToJson(getRecommendationsResponse), 200)));

    SpotifyPlaylistDto emptyPlaylist = SpotifyClientHelper.getPlaylistDto(userId);
    String playlistId = emptyPlaylist.id();

    stubFor(
        post(urlEqualTo("/v1/users/" + userId + "/playlists"))
            .willReturn(WireMock.jsonResponse(JsonHelper.objectToJson(emptyPlaylist), 200)));

    UpdatePlaylistItemsResponse updatePlaylistItemsResponse = SpotifyClientHelper.createUpdatePlaylistItemsResponse();

    stubFor(
        post(urlEqualTo("/v1/playlists/" + playlistId + "/tracks"))
            .willReturn(WireMock.jsonResponse(JsonHelper.objectToJson(updatePlaylistItemsResponse), 200)));

    SpotifyPlaylistDto playlist = SpotifyClientHelper.updatePlaylistDto(emptyPlaylist, musicRecommendations);

    stubFor(
        get(urlEqualTo("/v1/playlists/" + playlistId))
            .willReturn(WireMock.jsonResponse(JsonHelper.objectToJson(playlist), 200)));

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
  }*/
}
