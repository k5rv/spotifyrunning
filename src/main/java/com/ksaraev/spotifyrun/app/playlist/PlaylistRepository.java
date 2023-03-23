package com.ksaraev.spotifyrun.app.playlist;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, String> {

  boolean existsByRunnerId(String id);

  Optional<Playlist> findByRunnerId(String id);
}
