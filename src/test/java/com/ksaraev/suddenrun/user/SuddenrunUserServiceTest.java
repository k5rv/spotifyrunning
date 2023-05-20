package com.ksaraev.suddenrun.user;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.ksaraev.utils.helpers.SuddenrunHelper;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

class SuddenrunUserServiceTest {

  @Mock private SuddenrunUserRepository repository;

  @Captor private ArgumentCaptor<String> userIdArgumentCaptor;

  @Captor private ArgumentCaptor<SuddenrunUser> suddenrunUserArgumentCaptor;

  private AutoCloseable closeable;

  private AppUserService underTest;

  @BeforeEach
  void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
    underTest = new SuddenrunUserService(repository);
  }

  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  void itShouldReturnTrueIfUserIsRegistered() {
    // Given
    SuddenrunUser suddenrunUser = SuddenrunHelper.getUser();
    String id = suddenrunUser.getId();
    given(repository.existsById(id)).willReturn(true);

    // When
    underTest.isUserRegistered(id);

    // Then
    then(repository).should().existsById(userIdArgumentCaptor.capture());
    assertThat(userIdArgumentCaptor.getValue()).isEqualTo(id);
  }

  @Test
  void itShouldReturnFalseIfUserIsNotRegistered() {
    // Given
    SuddenrunUser suddenrunUser = SuddenrunHelper.getUser();
    String id = suddenrunUser.getId();
    given(repository.existsById(id)).willReturn(false);

    // When
    underTest.isUserRegistered(id);

    // Then
    then(repository).should().existsById(userIdArgumentCaptor.capture());
    assertThat(userIdArgumentCaptor.getValue()).isEqualTo(id);
  }

  @Test
  void
      itShouldThrowGetSuddenrunUserRegistrationStatusExceptionIfUserRepositoryThrowsRuntimeException() {
    // Given
    String message = "message";
    SuddenrunUser suddenrunUser = SuddenrunHelper.getUser();
    String id = suddenrunUser.getId();
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
    SuddenrunUser suddenrunUser = SuddenrunHelper.getUser();
    String id = suddenrunUser.getId();
    given(repository.findById(id)).willReturn(Optional.of(suddenrunUser));

    // When
    Optional<AppUser> appUser = underTest.getUser(id);

    // Then
    assertThat(appUser)
        .isPresent()
        .hasValueSatisfying(
            user -> assertThat((SuddenrunUser) user).usingRecursiveComparison().isEqualTo(suddenrunUser));
  }

  @Test
  void itShouldReturnEmptyOptionalIfUserIsNotPresent() {
    // Given
    SuddenrunUser suddenrunUser = SuddenrunHelper.getUser();
    String id = suddenrunUser.getId();
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
    SuddenrunUser suddenrunUser = SuddenrunHelper.getUser();
    String id = suddenrunUser.getId();
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
    SuddenrunUser suddenrunUser = SuddenrunHelper.getUser();
    String id = suddenrunUser.getId();
    String name = suddenrunUser.getName();

    // When
    underTest.registerUser(id, name);

    // Then
    then(repository).should().save(suddenrunUserArgumentCaptor.capture());
    SuddenrunUser suddenrunUserArgumentCaptureValue = suddenrunUserArgumentCaptor.getValue();
    assertThat(suddenrunUserArgumentCaptureValue).isEqualTo(suddenrunUser);
  }

  @Test
  void itShouldThrowRegisterSuddenrunUserExceptionIfUserRepositoryThrowsRuntimeException() {
    // Given
    String message = "message";
    SuddenrunUser suddenrunUser = SuddenrunHelper.getUser();
    String id = suddenrunUser.getId();
    String name = suddenrunUser.getName();
    given(repository.save(suddenrunUser)).willThrow(new RuntimeException(message));

    // Then
    assertThatThrownBy(() -> underTest.registerUser(id, name))
        .isExactlyInstanceOf(RegisterSuddenrunUserException.class)
        .hasMessageContaining(id)
        .hasMessageContaining(message);
  }
}
