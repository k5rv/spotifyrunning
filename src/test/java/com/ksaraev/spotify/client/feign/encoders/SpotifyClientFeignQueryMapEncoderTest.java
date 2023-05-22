package com.ksaraev.spotify.client.feign.encoders;

import static com.ksaraev.spotify.client.exception.SpotifyClientRequestEncodingException.UNABLE_TO_ENCODE_OBJECT_INTO_QUERY_MAP;
import static org.mockito.BDDMockito.*;

import com.ksaraev.spotify.client.exception.SpotifyClientRequestEncodingException;
import feign.Param;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

class SpotifyClientFeignQueryMapEncoderTest {

  private static final String PARAM_ANNOTATION_VALUE = "param_annotation_value";
  @Mock HashMap<String, Object> fieldNameToValue;
  private SpotifyClientFeignQueryMapEncoder underTest;

  private AutoCloseable closeable;

  @BeforeEach
  void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
    underTest = new SpotifyClientFeignQueryMapEncoder();
  }

  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  void itShouldEncodeClassFieldNameToLowerUnderscore() {
    // Given
    String fieldValue = "fieldValue";
    ClassToEncode classToEncode = new ClassToEncode(fieldValue);
    // Then
    Assertions.assertThat(underTest.encode(classToEncode))
        .isEqualTo(Map.of("field_name_to_encode", fieldValue));
  }

  @Test
  void itShouldEncodeClassFieldNameToParamAnnotationValueIfPresent() {
    // Given
    String fieldValue = "fieldValue";
    AnnotatedClassToEncode annotatedClassToEncode = new AnnotatedClassToEncode(fieldValue);
    // Then
    Assertions.assertThat(underTest.encode(annotatedClassToEncode))
        .isEqualTo(Map.of(PARAM_ANNOTATION_VALUE, fieldValue));
  }

  @Test
  void itShouldEncodeCustomClassFieldNameToLowerUnderscore() {
    // Given
    String customClassStringFieldValue = "customClassStringFieldValue";
    ClassToEncode classToEncode = new ClassToEncode(customClassStringFieldValue);
    ClassToEncodeWithCustomClassField classToEncodeWithCustomClassField =
        new ClassToEncodeWithCustomClassField(classToEncode);

    // Then
    Assertions.assertThat(underTest.encode(classToEncodeWithCustomClassField))
        .isEqualTo(Map.of("field_name_to_encode", customClassStringFieldValue));
  }

  @Test
  void itShouldEncodeCollectionClassFieldValuesToStringSeparatedByComma() {
    // Given
    List<String> elements = List.of("elementA", "elementB");
    ClassToEncodeWithCollectionTypeField classToEncodeWithCollectionTypeField =
        new ClassToEncodeWithCollectionTypeField(elements);
    // Then
    Assertions.assertThat(underTest.encode(classToEncodeWithCollectionTypeField))
        .isEqualTo(Map.of("collection_field_name_to_encode", "elementA,elementB"));
  }

  @Test
  void itShouldThrowSpotifyClientRequestEncodingExceptionWhenIllegalAccessExceptionThrown() {
    // Given
    String message = "message";
    String fieldValue = "fieldValue";
    ClassToEncode classToEncode = new ClassToEncode(fieldValue);
    given(
            fieldNameToValue.put(
                ArgumentMatchers.any(String.class), ArgumentMatchers.any(Object.class)))
        .willAnswer(
            invocation -> {
              throw new IllegalAccessException(message);
            });
    // Then
    Assertions.assertThatThrownBy(() -> underTest.encode(classToEncode, fieldNameToValue))
        .isExactlyInstanceOf(SpotifyClientRequestEncodingException.class)
        .hasCauseExactlyInstanceOf(IllegalAccessException.class)
        .hasMessage(UNABLE_TO_ENCODE_OBJECT_INTO_QUERY_MAP + message);
  }

  @Test
  void itShouldThrowSpotifyClientRequestEncodingExceptionWhenRuntimeExceptionThrown() {
    // Given
    String message = "message";
    String fieldValue = "fieldValue";
    ClassToEncode classToEncode = new ClassToEncode(fieldValue);
    given(
            fieldNameToValue.put(
                ArgumentMatchers.any(String.class), ArgumentMatchers.any(Object.class)))
        .willThrow(new RuntimeException(message));
    Assertions.assertThatThrownBy(() -> underTest.encode(classToEncode, fieldNameToValue))
        .isExactlyInstanceOf(SpotifyClientRequestEncodingException.class)
        .hasCauseExactlyInstanceOf(RuntimeException.class)
        .hasMessage(UNABLE_TO_ENCODE_OBJECT_INTO_QUERY_MAP + message);
  }

  private record ClassToEncode(String fieldNameToEncode) {}

  private record AnnotatedClassToEncode(@Param(PARAM_ANNOTATION_VALUE) String fieldNameToEncode) {}

  private record ClassToEncodeWithCustomClassField(ClassToEncode customClassFieldNameToEncode) {}

  private record ClassToEncodeWithCollectionTypeField(List<String> collectionFieldNameToEncode) {}
}
