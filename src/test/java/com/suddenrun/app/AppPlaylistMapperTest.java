package com.suddenrun.app;

import com.suddenrun.app.playlist.AppPlaylist;
import com.suddenrun.app.playlist.AppPlaylistMapper;
import com.suddenrun.app.playlist.AppPlaylistMapperImpl;
import com.suddenrun.app.playlist.Playlist;
import com.suddenrun.app.track.AppTrackMapperImpl;
import com.suddenrun.app.user.AppUser;
import com.suddenrun.app.user.AppUserMapper;
import com.suddenrun.app.user.AppUserMapperImpl;
import com.suddenrun.app.user.Runner;
import com.suddenrun.spotify.model.playlist.SpotifyPlaylistItem;
import com.suddenrun.spotify.model.userprofile.SpotifyUserProfileItem;
import com.suddenrun.utils.helpers.SpotifyServiceHelper;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {AppPlaylistMapperImpl.class, AppUserMapperImpl.class, AppTrackMapperImpl.class})
class AppPlaylistMapperTest {

  @Autowired AppPlaylistMapper underTest;
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
  }
}
