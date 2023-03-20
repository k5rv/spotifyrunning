package com.ksaraev.spotifyrun.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksaraev.spotifyrun.client.api.items.SpotifyUserProfileItem;
import com.ksaraev.spotifyrun.config.playlist.SpotifyRunPlaylistConfig;
import com.ksaraev.spotifyrun.model.playlist.Playlist;
import com.ksaraev.spotifyrun.model.playlist.PlaylistMapper;import com.ksaraev.spotifyrun.model.playlist.SpotifyPlaylist;
import com.ksaraev.spotifyrun.model.user.AppUser;
import com.ksaraev.spotifyrun.model.user.AppUserMapper;
import com.ksaraev.spotifyrun.model.user.SpotifyUser;
import com.ksaraev.spotifyrun.repository.SpotifyRunPlaylistRepository;
import com.ksaraev.spotifyrun.repository.SpotifyRunUserRepository;
import com.ksaraev.spotifyrun.security.AppAuthPrinciple;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpotifyRunUserService implements AppUserService {

  private final AppAuthPrinciple appAuthPrinciple;
  private final SpotifyRunUserRepository spotifyRunUserRepository;
  private final AppUserMapper appUserMapper;
  private final SpotifyPlaylistService spotifyPlaylistService;

  private final SpotifyRunPlaylistConfig playlistConfig;

  private final SpotifyRunPlaylistRepository spotifyRunPlaylistRepository;

  private final PlaylistMapper playlistMapper;

  @Override
  public boolean hasPlaylist(SpotifyUser spotifyUser) {
    return spotifyRunPlaylistRepository.existsByOwner((AppUser) spotifyUser);
  }

  @Override
  public SpotifyPlaylist createPlaylist(SpotifyUser spotifyUser) {
    SpotifyPlaylist playlist =
        spotifyPlaylistService.createPlaylist(spotifyUser, playlistConfig.getDetails());
    return spotifyRunPlaylistRepository.save((Playlist) playlist);
  }

  public boolean isUserRegistered(String id) {
    return spotifyRunUserRepository.existsById(id);
  }

  public SpotifyUser getAuthenticatedUser() {
    OAuth2AuthenticatedPrincipal principal = appAuthPrinciple.getOAuth2AuthenticatedPrincipal();
    ObjectMapper mapper = new ObjectMapper();
    SpotifyUserProfileItem spotifyUserProfileItem =
        mapper.convertValue(principal.getAttributes(), SpotifyUserProfileItem.class);
    return appUserMapper.mapToUser(spotifyUserProfileItem);
  }

  @Override
  public void registerUser(SpotifyUser spotifyUser) {
    spotifyRunUserRepository.save((AppUser) spotifyUser);
  }
}
