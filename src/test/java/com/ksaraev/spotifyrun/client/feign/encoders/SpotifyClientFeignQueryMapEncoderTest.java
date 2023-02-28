package com.ksaraev.spotifyrun.client.feign.encoders;

import feign.Param;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class SpotifyClientFeignQueryMapEncoderTest {

  private static final String PARAM_ANNOTATION_VALUE = "param_annotation_value";
  private SpotifyClientFeignQueryMapEncoder underTest;

  @BeforeEach
  void setUp() {
    underTest = new SpotifyClientFeignQueryMapEncoder();
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
  void itShouldEncodeCustomClassFieldNameToLowerUnderscore() throws Exception {
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

  private record ClassToEncode(String fieldNameToEncode) {}

  private record AnnotatedClassToEncode(@Param(PARAM_ANNOTATION_VALUE) String fieldNameToEncode) {}

  private record ClassToEncodeWithCustomClassField(ClassToEncode customClassFieldNameToEncode) {}

  private record ClassToEncodeWithCollectionTypeField(List<String> collectionFieldNameToEncode) {}
}
