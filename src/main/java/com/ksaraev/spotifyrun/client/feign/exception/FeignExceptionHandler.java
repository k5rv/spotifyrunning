package com.ksaraev.spotifyrun.client.feign.exception;

import feign.Response;

public interface FeignExceptionHandler {
  Exception handle(Response response);
}
