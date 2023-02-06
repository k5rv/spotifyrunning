package com.ksaraev.spotifyrun.client.exception;

import feign.Response;

public interface ClientExceptionHandler {
  Exception handle(Response response);
}
