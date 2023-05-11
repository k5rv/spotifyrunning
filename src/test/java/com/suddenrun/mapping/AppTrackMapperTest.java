package com.suddenrun.mapping;

import com.ksaraev.spotifyrun.app.track.AppTrack;import com.ksaraev.spotifyrun.app.track.AppTrackMapper;import com.ksaraev.spotifyrun.app.track.AppTrackMapperImpl;import com.ksaraev.spotifyrun.app.track.Track;
import com.ksaraev.spotifyrun.spotify.model.SpotifyItemType;
import com.ksaraev.spotifyrun.spotify.model.track.SpotifyTrackItem;
import java.net.URI;
import com.suddenrun.utils.SpotifyHelper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AppTrackMapperImpl.class})
class AppTrackMapperTest {

  @Autowired AppTrackMapper underTest;

  @Test
  void itShouldMapDtoToEntity() {
    // Given
    SpotifyTrackItem trackItem = SpotifyHelper.getTrack();
    AppTrack appTrack = underTest.mapToEntity(trackItem);
    // Then
    Assertions.assertThat(appTrack.getId()).isEqualTo(trackItem.getId());
    Assertions.assertThat(appTrack.getName()).isEqualTo(trackItem.getName());
  }

  @Test
  void itShouldMapEntityToDto() {
    // Given
    String id = "203gf072g307fg072g02f";
    URI uri = SpotifyItemType.TRACK.createUri(id);
    String name = "name";
    AppTrack appTrack = Track.builder().id(id).name(name).build();
    SpotifyTrackItem trackItem = underTest.mapToDto(appTrack);
    // Then
    Assertions.assertThat(trackItem.getId()).isEqualTo(appTrack.getId());
    Assertions.assertThat(trackItem.getName()).isEqualTo(appTrack.getName());
    Assertions.assertThat(trackItem.getUri()).isEqualTo(uri);
  }
}
