package com.ksaraev.spotifyrun.client.feign.exception;

import lombok.experimental.StandardException;

@StandardException
public class SpotifyTooManyRequestsException extends SpotifyWebApiException {}
