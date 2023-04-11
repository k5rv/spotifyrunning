package com.ksaraev.spotifyrun.client.feign.exception;

import lombok.experimental.StandardException;

@StandardException
public class SpotifyServiceUnavailableException extends SpotifyWebApiException {}
