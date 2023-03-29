package com.ksaraev.spotifyrun.app.playlist;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, String> {

  boolean existsByRunnerId(String id);

  Optional<Playlist> findByRunnerId(String id);

  void deleteByIdAndSnapshotId(String id, String snapshotId);
}
