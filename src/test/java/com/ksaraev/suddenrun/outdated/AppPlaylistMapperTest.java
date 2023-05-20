package com.ksaraev.suddenrun.outdated;

/*@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {AppPlaylistMapperImpl.class, AppUserMapperImpl.class, AppTrackMapperImpl.class})*/
class AppPlaylistMapperTest {

/*  @Autowired AppPlaylistMapper underTest;
  @Autowired AppUserMapper appUserMapper;

  @Test
  void itShouldMapDtoToEntity() {
    // Given
    SpotifyPlaylistItem playlistItem = SpotifyServiceHelper.getPlaylist();
    AppPlaylist appPlaylist = underTest.mapToEntity(playlistItem);
    SpotifyUserProfileItem userProfileItem = playlistItem.getOwner();
    AppUser appUser = appUserMapper.mapToEntity(userProfileItem);
    // Then
    Assertions.assertThat(appPlaylist.getId()).isEqualTo(playlistItem.getId());
    Assertions.assertThat(appPlaylist.getOwner()).isEqualTo(appUser);
  }

  @Test
  void itShouldMapEntityToDto() {
    String playlistId = "289346892364985";
    String playlistSnapshotId = "ds87fg7wgf8g2873gf8f23g";
    String userId = "9000000000000";
    String userName = "konstantin";
    Runner runner = Runner.builder().id(userId).name(userName).build();
    AppPlaylist appPlaylist =
        Playlist.builder()
            .id(playlistId)
            .snapshotId(playlistSnapshotId)
            .runner(runner)
            .tracks(List.of())
            .build();

    SpotifyPlaylistItem playlistItem = underTest.mapToDto(appPlaylist);
    Assertions.assertThat(playlistItem.getId()).isEqualTo(playlistId);
    Assertions.assertThat(playlistItem.getSnapshotId()).isEqualTo(playlistSnapshotId);
    SpotifyUserProfileItem playlistItemOwner = playlistItem.getOwner();
    Assertions.assertThat(playlistItemOwner).isEqualTo(appUserMapper.mapToDto(runner));
  }*/
}
