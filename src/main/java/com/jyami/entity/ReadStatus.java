package com.jyami.entity;

import java.time.LocalDateTime;

public class ReadStatus {
    private final long id;
    private final long userId;
    private final long channelId;

    private final LocalDateTime lastReadAt;

    public ReadStatus(long id, long userId, long channelId, LocalDateTime lastReadAt) {
        this.id = id;
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = lastReadAt;
    }

    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public long getChannelId() {
        return channelId;
    }

    public LocalDateTime getLastReadAt() {
        return lastReadAt;
    }
}
