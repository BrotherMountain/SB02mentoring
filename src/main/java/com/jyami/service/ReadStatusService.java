package com.jyami.service;

import com.jyami.dto.ReadStatusDto;
import com.jyami.entity.ReadStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReadStatusService {

    private final Map<Long, ReadStatus> db = new HashMap<>();

    public ReadStatusDto createReadStatus(ReadStatusDto dto) {
        System.out.println("Create read status");
        db.put(dto.id(), new ReadStatus(dto.id(), dto.userId(), dto.channelId(), dto.lastReadAt()));
        return null;
    }

    public ReadStatusDto findById(Long id) {
        System.out.println("Find read status by id");
        return null;
    }


}
