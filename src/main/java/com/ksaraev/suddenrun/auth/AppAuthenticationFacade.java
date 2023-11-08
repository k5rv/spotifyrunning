package com.ksaraev.suddenrun.auth;

import org.springframework.security.core.Authentication;

public interface AppAuthenticationFacade {
  Authentication getAuthentication();

}
