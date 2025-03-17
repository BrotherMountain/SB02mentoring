package com.jyami.controller;

import com.jyami.dto.ReadStatusDto;
import com.jyami.service.ReadStatusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/read")
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    public ReadStatusController(ReadStatusService readStatusService) {
        this.readStatusService = readStatusService;
    }

    // [CREATE]
    @PostMapping
    public ResponseEntity<ReadStatusDto> create(@RequestBody ReadStatusDto dto) {
        ReadStatusDto createdStatus = readStatusService.createReadStatus(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStatus);
    }

    // [READ] ID 조회
    @GetMapping("/{id}")
    public ResponseEntity<ReadStatusDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(readStatusService.findById(id));
    }
}