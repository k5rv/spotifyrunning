package com.ksaraev.spotifyrun.security;

import org.springframework.security.core.Authentication;

public interface AuthenticationFacade {
  Authentication getAuthentication();
}
