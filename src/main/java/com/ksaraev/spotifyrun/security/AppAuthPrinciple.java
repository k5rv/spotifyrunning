package com.ksaraev.spotifyrun.security;

import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;

public interface AppAuthPrinciple {

  OAuth2AuthenticatedPrincipal getOAuth2AuthenticatedPrincipal();
}
