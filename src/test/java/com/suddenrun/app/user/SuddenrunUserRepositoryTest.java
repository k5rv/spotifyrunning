package com.suddenrun.app.user;

import static org.assertj.core.api.Assertions.*;

import com.suddenrun.utils.helpers.SpotifyResourceHelper;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest()
class SuddenrunUserRepositoryTest {

  @Autowired private RunnerRepository underTest;

  @Test
  void itShouldReturnTrueIfRunnerWithIdExists() {
    // Given
    String id = SpotifyResourceHelper.getRandomId();
    String name = SpotifyResourceHelper.getRandomName();
    Runner runner = Runner.builder().id(id).name(name).build();
    underTest.save(runner);

    // When
    boolean isExists = underTest.existsById(id);

    // Then
    assertThat(isExists).isTrue();
  }

  @Test
  void itShouldReturnFalseIfRunnerWithIdDoesNotExists() {
    // Given
    String id = SpotifyResourceHelper.getRandomId();

    // When
    boolean isExists = underTest.existsById(id);

    // Then
    assertThat(isExists).isFalse();
  }

  @Test
  void itShouldSaveRunner() {
    // Given
    String id = SpotifyResourceHelper.getRandomId();
    String name = SpotifyResourceHelper.getRandomName();
    Runner runner = Runner.builder().id(id).name(name).build();

    // When
    underTest.save(runner);

    // Then
    Optional<Runner> optionalRunner = underTest.findById(id);
    assertThat(optionalRunner)
        .isPresent()
        .hasValueSatisfying(r -> assertThat(r).usingRecursiveComparison().isEqualTo(runner));
  }

  @Test
  void itShouldFindRunnerById() {
    // Given
    String id = SpotifyResourceHelper.getRandomId();
    String name = SpotifyResourceHelper.getRandomName();
    Runner runner = Runner.builder().id(id).name(name).build();
    underTest.save(runner);

    // When
    Optional<Runner> optionalRunner = underTest.findById(id);

    // Then
    assertThat(optionalRunner)
        .isPresent()
        .hasValueSatisfying(r -> assertThat(r).usingRecursiveComparison().isEqualTo(runner));
  }
}
