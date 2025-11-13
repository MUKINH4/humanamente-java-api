package com.humanamente.api.dto;

public record ApiResponse<T>(
    String message,
    T data
) {}
