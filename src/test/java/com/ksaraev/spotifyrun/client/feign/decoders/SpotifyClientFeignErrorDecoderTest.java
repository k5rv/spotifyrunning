package com.ksaraev.spotifyrun.client.feign.decoders;

import static com.ksaraev.spotifyrun.client.exceptions.SpotifyClientDecodingErrorResponseIsNullException.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.exceptions.SpotifyClientDecodingErrorResponseIsNullException;
import com.ksaraev.spotifyrun.client.feign.exception.FeignExceptionHandler;
import com.ksaraev.spotifyrun.client.feign.exception.SpotifyClientFeignExceptionHandler;
import com.ksaraev.spotifyrun.client.feign.exception.http.SpotifyException;
import feign.*;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;

class SpotifyClientFeignErrorDecoderTest {

  @Mock private SpotifyClient spotifyClient;
  @Mock private ApplicationContext applicationContext;

  @Mock private Map<String, FeignExceptionHandler> feignExceptionHandlers;

  @Mock private SpotifyClientFeignExceptionHandler spotifyClientFeignExceptionHandler;
  private SpotifyClientFeignErrorDecoder underTest;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    underTest = new SpotifyClientFeignErrorDecoder(applicationContext, feignExceptionHandlers);
  }

  @Test
  void itShouldReturnFeignExceptionWhenSpotifyClientFeignExceptionHandlerNotFound() {
    // Given
    String methodKey = "SpotifyClient#getSomething()";
    Response response =
        Response.builder()
            .body("", StandardCharsets.UTF_8)
            .request(
                Request.create(
                    Request.HttpMethod.GET,
                    "http://127.0.0.1",
                    Map.of(),
                    Request.Body.empty(),
                    new RequestTemplate()))
            .status(400)
            .build();
    given(feignExceptionHandlers.get(methodKey)).willReturn(null);
    // Then
    assertThat(underTest.decode(methodKey, response)).isInstanceOf(FeignException.class);
  }

  @Test
  void itShouldReturnSpotifyExceptionWhenSpotifyClientFeignExceptionHandlerFound() {
    // Given
    String methodKey = "SpotifyClient#getSomething()";
    Response response =
        Response.builder()
            .body("", StandardCharsets.UTF_8)
            .request(
                Request.create(
                    Request.HttpMethod.GET,
                    "http://127.0.0.1",
                    Map.of(),
                    Request.Body.empty(),
                    new RequestTemplate()))
            .status(400)
            .build();
    given(feignExceptionHandlers.get(methodKey))
        .willReturn(new SpotifyClientFeignExceptionHandler());
    // Then
    assertThat(underTest.decode(methodKey, response)).isInstanceOf(SpotifyException.class);
  }

  @Test
  void itShouldReturnFeignExceptionWhenSpotifyClientFeignExceptionHandlerReturnsNull() {
    // Given
    String methodKey = "SpotifyClient#getSomething()";
    Response response =
        Response.builder()
            .body("", StandardCharsets.UTF_8)
            .request(
                Request.create(
                    Request.HttpMethod.GET,
                    "http://127.0.0.1",
                    Map.of(),
                    Request.Body.empty(),
                    new RequestTemplate()))
            .status(400)
            .build();
    given(spotifyClientFeignExceptionHandler.handle(response)).willReturn(null);
    given(feignExceptionHandlers.get(methodKey)).willReturn(spotifyClientFeignExceptionHandler);
    // Then
    assertThat(underTest.decode(methodKey, response)).isInstanceOf(FeignException.class);
  }

  @Test
  void itShouldThrowSpotifyClientExceptionWhenResponseIsNull() {
    // Then
    assertThatThrownBy(() -> underTest.decode("SpotifyClient#getSomething()", null))
        .isExactlyInstanceOf(SpotifyClientDecodingErrorResponseIsNullException.class)
        .hasMessage(DECODING_ERROR_RESPONSE_IS_NULL);
  }

  @Test
  void itShouldCollectFeignExceptionHandlersIfPresent() {
    // Given
    List<Method> actualSpotifyFeignClientMethods =
        Arrays.stream(spotifyClient.getClass().getInterfaces()[0].getDeclaredMethods()).toList();

    List<String> configKeys =
        actualSpotifyFeignClientMethods.stream()
            .map(method -> Feign.configKey(method.getDeclaringClass(), method))
            .toList();

    given(applicationContext.getBeansWithAnnotation(FeignClient.class))
        .willReturn(Map.of(SpotifyClient.class.getCanonicalName(), spotifyClient));

    given(applicationContext.getBean(SpotifyClientFeignExceptionHandler.class))
        .willReturn(new SpotifyClientFeignExceptionHandler());

    ArgumentCaptor<String> configKeyArgumentCaptor = ArgumentCaptor.forClass(String.class);

    ArgumentCaptor<SpotifyClientFeignExceptionHandler> handlerBeanArgumentCaptor =
        ArgumentCaptor.forClass(SpotifyClientFeignExceptionHandler.class);

    // When
    underTest.onApplicationEvent(new ContextRefreshedEvent(applicationContext));

    // Then
    then(feignExceptionHandlers)
        .should(times(actualSpotifyFeignClientMethods.size()))
        .put(configKeyArgumentCaptor.capture(), handlerBeanArgumentCaptor.capture());
    assertThat(configKeyArgumentCaptor.getAllValues()).hasSameElementsAs(configKeys);
    assertThat(handlerBeanArgumentCaptor.getAllValues())
        .hasOnlyElementsOfType(SpotifyClientFeignExceptionHandler.class);
  }
}
