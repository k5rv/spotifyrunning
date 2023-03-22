package com.ksaraev.spotifyrun.app.user;

import java.util.UUID;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RunnerRepository extends JpaRepository<Runner, UUID> {

  boolean existsBySpotifyId(@NonNull String spotifyId);

  Runner findBySpotifyId(@NonNull String id);
}
