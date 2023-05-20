package com.suddenrun.app.user;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.suddenrun.utils.helpers.SuddenrunHelper;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

class RunnerServiceTest {

  @Mock private RunnerRepository repository;

  @Captor private ArgumentCaptor<String> idArgumentCaptor;
  @Captor private ArgumentCaptor<Runner> runnerArgumentCaptor;

  private AutoCloseable closeable;

  private AppUserService underTest;

  @BeforeEach
  void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
    underTest = new RunnerService(repository);
  }

  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  void itShouldReturnTrueIfUserIsRegistered() {
    // Given
    Runner runner = (Runner) SuddenrunHelper.getUser();
    String id = runner.getId();
    given(repository.existsById(id)).willReturn(true);

    // When
    underTest.isUserRegistered(id);

    // Then
    then(repository).should().existsById(idArgumentCaptor.capture());
    assertThat(idArgumentCaptor.getValue()).isEqualTo(id);
  }

  @Test
  void itShouldReturnFalseIfUserIsNotRegistered() {
    // Given
    Runner runner = (Runner) SuddenrunHelper.getUser();
    String id = runner.getId();
    given(repository.existsById(id)).willReturn(false);

    // When
    underTest.isUserRegistered(id);

    // Then
    then(repository).should().existsById(idArgumentCaptor.capture());
    assertThat(idArgumentCaptor.getValue()).isEqualTo(id);
  }

  @Test
  void
      itShouldThrowGetSuddenrunUserRegistrationStatusExceptionIfUserRepositoryThrowsRuntimeException() {
    // Given
    String message = "message";
    Runner runner = (Runner) SuddenrunHelper.getUser();
    String id = runner.getId();
    given(repository.existsById(id)).willThrow(new RuntimeException(message));

    // Then
    assertThatThrownBy(() -> underTest.isUserRegistered(id))
        .isExactlyInstanceOf(GetSuddenrunUserRegistrationStatusException.class)
        .hasMessageContaining(id)
        .hasMessageContaining(message);
  }

  @Test
  void itShouldReturnUserIfItIsPresent() {
    // Given
    Runner runner = (Runner) SuddenrunHelper.getUser();
    String id = runner.getId();
    given(repository.findById(id)).willReturn(Optional.of(runner));

    // When
    Optional<AppUser> appUser = underTest.getUser(id);

    // Then
    assertThat(appUser)
        .isPresent()
        .hasValueSatisfying(
            user -> assertThat((Runner) user).usingRecursiveComparison().isEqualTo(runner));
  }

  @Test
  void itShouldReturnEmptyOptionalIfUserIsNotPresent() {
    // Given
    Runner runner = (Runner) SuddenrunHelper.getUser();
    String id = runner.getId();
    given(repository.findById(id)).willReturn(Optional.empty());

    // When
    Optional<AppUser> appUser = underTest.getUser(id);

    // Then
    assertThat(appUser).isNotPresent();
  }

  @Test
  void itShouldThrowGetSuddenrunUserExceptionIfUserRepositoryThrowsRuntimeException() {
    // Given
    String message = "message";
    Runner runner = (Runner) SuddenrunHelper.getUser();
    String id = runner.getId();
    given(repository.findById(id)).willThrow(new RuntimeException(message));

    // Then
    assertThatThrownBy(() -> underTest.getUser(id))
        .isExactlyInstanceOf(GetSuddenrunUserException.class)
        .hasMessageContaining(id)
        .hasMessageContaining(message);
  }

  @Test
  void itShouldRegisterUser() {
    // Given
    Runner runner = (Runner) SuddenrunHelper.getUser();
    String id = runner.getId();
    String name = runner.getName();

    // When
    underTest.registerUser(id, name);

    // Then
    then(repository).should().save(runnerArgumentCaptor.capture());
    Runner runnerArgumentCaptureValue = runnerArgumentCaptor.getValue();
    assertThat(runnerArgumentCaptureValue).isEqualTo(runner);
  }

  @Test
  void itShouldThrowRegisterSuddenrunUserExceptionIfUserRepositoryThrowsRuntimeException() {
    // Given
    String message = "message";
    Runner runner = (Runner) SuddenrunHelper.getUser();
    String id = runner.getId();
    String name = runner.getName();
    given(repository.save(runner)).willThrow(new RuntimeException(message));

    // Then
    assertThatThrownBy(() -> underTest.registerUser(id, name))
        .isExactlyInstanceOf(RegisterSuddenrunUserException.class)
        .hasMessageContaining(id)
        .hasMessageContaining(message);
  }
}
