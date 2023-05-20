package com.ksaraev.suddenrun.playlist;

import java.util.Optional;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, String> {

  boolean existsBySuddenrunUserId(String userId);

  Optional<Playlist> findBySuddenrunUserId(String userId);

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
  @Query("DELETE FROM Playlist p WHERE p.suddenrunUser.id = :userId")
  void deleteBySuddenrunUserId(@NonNull String userId);
}
