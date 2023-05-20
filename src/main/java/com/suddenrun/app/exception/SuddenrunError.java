package com.suddenrun.app.exception;

import lombok.Builder;

@Builder
public record SuddenrunError(Integer status, String message) {}
