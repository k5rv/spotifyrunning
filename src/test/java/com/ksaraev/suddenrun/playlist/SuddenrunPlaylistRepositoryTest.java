package com.ksaraev.suddenrun.playlist;

import static org.assertj.core.api.Assertions.*;

import com.ksaraev.suddenrun.user.AppUser;
import com.ksaraev.suddenrun.user.SuddenrunUser;
import com.ksaraev.suddenrun.user.SuddenrunUserRepository;
import com.ksaraev.utils.helpers.SuddenrunHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import org.hibernate.HibernateException;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest()
class SuddenrunPlaylistRepositoryTest {

  @Autowired private SuddenrunPlaylistRepository underTest;

  @Autowired EntityManager entityManager;

  @Test
  void itShouldSavePlaylist() {
    // Given
    SuddenrunUser user = SuddenrunHelper.getUser();
    SuddenrunPlaylist playlist = SuddenrunHelper.getSuddenrunPlaylist(user);
    user.setPlaylists(List.of(playlist));
    entityManager.persist(user);

    // When
    SuddenrunPlaylist result = underTest.save(playlist);

    // Then
    assertThat(result).usingRecursiveComparison().isEqualTo(playlist);
  }

  @Test
  void itShouldFindPlaylistById() {
    // Given
    SuddenrunUser user = SuddenrunHelper.getUser();
    SuddenrunPlaylist playlist = SuddenrunHelper.getSuddenrunPlaylist(user);
    String playlistId = playlist.getId();
    user.setPlaylists(List.of(playlist));
    entityManager.persist(user);
    underTest.save(playlist);

    // When
    Optional<SuddenrunPlaylist> optionalPlaylist = underTest.findById(playlistId);

    // Then
    assertThat(optionalPlaylist)
        .isPresent()
        .hasValueSatisfying(p -> assertThat(p).usingRecursiveComparison().isEqualTo(playlist));
  }

  @Test
  void itShouldFindPlaylistByOwnerId() {
    // Given
    SuddenrunUser user = SuddenrunHelper.getUser();
    String userId = user.getId();
    entityManager.persist(user);
    SuddenrunPlaylist playlist = SuddenrunHelper.getSuddenrunPlaylist(user);
    underTest.save(playlist);

    // When
    Optional<SuddenrunPlaylist> optionalPlaylist = underTest.findByOwnerId(userId);

    // Then
    assertThat(optionalPlaylist)
        .isPresent()
        .hasValueSatisfying(p -> assertThat(p).usingRecursiveComparison().isEqualTo(playlist));
  }
}
