package com.ksaraev.spotifyrunning.client.exception;

import feign.Response;

public interface FeignExceptionHandler {
  Exception handle(Response response);
}
