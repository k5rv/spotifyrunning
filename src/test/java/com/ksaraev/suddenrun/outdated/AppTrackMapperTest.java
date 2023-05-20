package com.ksaraev.suddenrun.outdated;

/*@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AppTrackMapperImpl.class})*/
class AppTrackMapperTest {
/*
  @Autowired AppTrackMapper underTest;

  @Test
  void itShouldMapDtoToEntity() {
    // Given
    SpotifyTrackItem trackItem = SpotifyServiceHelper.getTrack();
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
  }*/
}
