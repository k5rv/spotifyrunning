package com.ksaraev.suddenrun.playlist;

import lombok.Builder;

import java.util.List;

@Builder
public record AddPlaylistTracksResponse(String id, List<String> trackIds) {}
