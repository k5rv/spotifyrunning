package com.suddenrun.app.exception;

import lombok.Builder;

@Builder
public record AppError(Integer status, String message) {}
