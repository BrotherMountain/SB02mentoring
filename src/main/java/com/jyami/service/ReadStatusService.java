package com.jyami.service;

import com.jyami.dto.ReadStatusDto;
import com.jyami.entity.ReadStatus;
import com.jyami.mapper.ReadStatusMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReadStatusService {

    private final ReadStatusMapper readStatusMapper;
    private final Map<Long, ReadStatus> db = new HashMap<>();

    public ReadStatusService(ReadStatusMapper readStatusMapper) {
        this.readStatusMapper = readStatusMapper;
    }

    public ReadStatusDto createReadStatus(ReadStatusDto dto) {
        ReadStatus entity = readStatusMapper.toEntity(dto);
        System.out.println("Create read status entity" + entity);
        db.put(dto.id(), new ReadStatus(dto.id(), dto.userId(), dto.channelId(), dto.lastReadAt()));
        return null;
    }

    public ReadStatusDto findById(Long id) {
        System.out.println("Find read status by id");
        ReadStatus readStatus = db.get(id);
        if (readStatus != null) {
            return readStatusMapper.toDto(readStatus);
        }
        return null;
    }


}
