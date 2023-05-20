package com.ksaraev.suddenrun.user;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.ksaraev.spotify.client.dto.SpotifyUserProfileDto;
import com.ksaraev.utils.helpers.JsonHelper;
import com.ksaraev.utils.helpers.SpotifyClientHelper;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ActiveProfiles(value = "test")
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SuddenrunUserIntegrationTest {

  private static final String SPOTIFY_API_V1_ME = "/v1/me";

  private static final String SUDDENRUN_API_V1_USERS = "/api/v1/users";

  @Autowired private MockMvc mockMvc;

  @Autowired
  AppUserService appUserService;

  @Test
  void itShouldRegisterUser() throws Exception {
    // Given
    SpotifyUserProfileDto userProfileDto = SpotifyClientHelper.getUserProfileDto();
    String id = userProfileDto.id();
    String name = userProfileDto.displayName();

    WireMock.stubFor(
        WireMock.get(WireMock.urlEqualTo(SPOTIFY_API_V1_ME))
            .willReturn(
                WireMock.jsonResponse(
                    JsonHelper.objectToJson(userProfileDto), HttpStatus.OK.value())));

    // When
    ResultActions userRegistrationResultActions =
        mockMvc.perform(
            MockMvcRequestBuilders.post(SUDDENRUN_API_V1_USERS)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf()));
    // Then
    userRegistrationResultActions
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id))
        .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(name));
  }

  @Test
  void itShouldThrowSuddenrunUserIsAlreadyRegisteredExceptionIfUserIsAlreadyRegistered()
      throws Exception {
    // Given
    SpotifyUserProfileDto userProfileDto = SpotifyClientHelper.getUserProfileDto();
    String id = userProfileDto.id();
    String name = userProfileDto.displayName();

    appUserService.registerUser(id, name);

    WireMock.stubFor(
        WireMock.get(WireMock.urlEqualTo(SPOTIFY_API_V1_ME))
            .willReturn(
                WireMock.jsonResponse(
                    JsonHelper.objectToJson(userProfileDto), HttpStatus.OK.value())));

    // When
    ResultActions userRegistrationRepeatedAttemptResultActions =
        mockMvc.perform(
            MockMvcRequestBuilders.post(SUDDENRUN_API_V1_USERS)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf()));

    // Then
    userRegistrationRepeatedAttemptResultActions.andExpect(
        MockMvcResultMatchers.status().isConflict());
  }

  @Test
  void itShouldReturnAuthenticationErrorWhenSpotifyAuthorizationFailed() throws Exception {
    // Given
    WireMock.stubFor(
        WireMock.get(WireMock.urlEqualTo(SPOTIFY_API_V1_ME))
            .willReturn(
                WireMock.jsonResponse(HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.value())));

    // When
    ResultActions userRegistrationResultActions =
        mockMvc.perform(
            MockMvcRequestBuilders.post(SUDDENRUN_API_V1_USERS)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf()));
    // Then
    userRegistrationResultActions.andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }
}
