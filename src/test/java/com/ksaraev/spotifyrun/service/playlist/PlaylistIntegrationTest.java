package com.ksaraev.spotifyrun.service.playlist;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureWireMock(port = 0)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class PlaylistIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void itShouldGetPlaylist() throws Exception {
    ResultActions getPlaylistresultActions = mockMvc.perform(post("/api/v1/playlists"));
    getPlaylistresultActions.andExpectAll(MockMvcResultMatchers.status().isOk());
  }
}
