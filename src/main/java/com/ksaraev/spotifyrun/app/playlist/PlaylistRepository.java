package com.ksaraev.spotifyrun.app.playlist;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, UUID> {

  boolean existsByRunnerUuid(UUID uuid);

  AppPlaylist findByRunnerUuid(UUID uuid);
}
