package com.ksaraev.spotifyrun.repository;

import com.ksaraev.spotifyrun.model.user.AppUser;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpotifyRunUserRepository extends JpaRepository<AppUser, String> {

  boolean existsById(@NonNull String id);
}
