package com.ksaraev.spotifyrun.app.runner;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface RunnerRepository extends JpaRepository<Runner, UUID> {

  boolean existsBySpotifyId(@NonNull String string);
}
