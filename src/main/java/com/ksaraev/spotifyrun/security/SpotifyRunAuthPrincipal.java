package com.ksaraev.spotifyrun.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Component;

@Component
public class SpotifyRunAuthPrincipal implements AppAuthPrinciple {

  @Override
  public OAuth2AuthenticatedPrincipal getOAuth2AuthenticatedPrincipal() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) return null;
    if (!authentication.isAuthenticated()) return null;
    return (OAuth2AuthenticatedPrincipal) authentication.getPrincipal();
  }
}
