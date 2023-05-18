package com.suddenrun.app.playlist;

import java.util.Optional;
import lombok.NonNull;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, String> {

  boolean existsByRunnerId(String id);

  Optional<Playlist> findByRunnerId(String id);

  @Transactional
  @Modifying
  @Query("DELETE FROM Playlist p WHERE p.id = :id")
  void deleteById(@NonNull String id);

  @Transactional
  @Modifying
  @Query("DELETE FROM Playlist p WHERE p.id = :id AND p.snapshotId=:snapshotId")
  void deleteByIdAndSnapshotId(@NonNull String id, @NonNull String snapshotId);

  @Transactional
  @Modifying
  @Query("DELETE FROM Playlist p WHERE p.runner.id = :runnerId")
  void deleteByRunnerId(@NonNull String runnerId);
}
