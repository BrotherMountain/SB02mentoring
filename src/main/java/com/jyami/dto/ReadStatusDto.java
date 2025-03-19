package com.jyami.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ReadStatusDto(
    long id,
    long userId,
    long channelId,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime lastReadAt) {
}
