package com.jyami.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record ReadStatusDto(
        long id,
        long userId,
        long channelId,
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime lastReadAt) {
}
