package com.ksaraev.suddenrun.auth;

import lombok.Builder;
import lombok.NonNull;
import org.springframework.security.oauth2.core.OAuth2Token;

@Builder
public class SuddenrunAuthentication {

  @NonNull private OAuth2Token oAuth2AccessToken;
  @NonNull private OAuth2Token oAuth2RefreshToken;
}
