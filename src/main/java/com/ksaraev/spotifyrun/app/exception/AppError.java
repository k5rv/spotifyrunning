package com.ksaraev.spotifyrun.app.exception;

import lombok.Builder;

@Builder
public record AppError(Integer status, String message) {}
