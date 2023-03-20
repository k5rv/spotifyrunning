package com.ksaraev.spotifyrun.repository;

import com.ksaraev.spotifyrun.model.playlist.Playlist;
import com.ksaraev.spotifyrun.model.user.AppUser;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpotifyRunPlaylistRepository extends JpaRepository<Playlist, String> {

  boolean existsByOwner(@NonNull AppUser owner);
}
