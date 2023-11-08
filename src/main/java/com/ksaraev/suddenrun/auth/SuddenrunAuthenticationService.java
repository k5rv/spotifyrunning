package com.ksaraev.suddenrun.auth;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuddenrunAuthenticationService {

  private final AppAuthenticationFacade suddenrunAuthenticationFacade;

  private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

  public void saveAuthentication() {
    OAuth2AuthenticatedPrincipal principal =
        (OAuth2AuthenticatedPrincipal)
            suddenrunAuthenticationFacade.getAuthentication().getPrincipal();
    Map<String, Object> attributes = principal.getAttributes();
    OAuth2AuthorizedClient oAuth2AuthorizedClient =
        oAuth2AuthorizedClientService.loadAuthorizedClient(
            "spotify", attributes.get("display_name").toString());

    OAuth2Token accessToken = oAuth2AuthorizedClient.getAccessToken();
    OAuth2Token refreshToken = oAuth2AuthorizedClient.getRefreshToken();
    SuddenrunAuthentication suddenrunAuthentication =
        SuddenrunAuthentication.builder()
            .oAuth2AccessToken(accessToken)
            .oAuth2RefreshToken(refreshToken)
            .build();
  }
}
