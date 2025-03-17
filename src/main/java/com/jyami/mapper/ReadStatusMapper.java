package com.jyami.mapper;

import com.jyami.dto.ReadStatusDto;
import com.jyami.entity.ReadStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReadStatusMapper {

    // Entity → DTO 변환
    ReadStatusDto toDto(ReadStatus readStatus);

    // DTO → Entity 변환 (필요한 경우)
    @Mapping(target = "id", ignore = true) // ID는 자동 생성되므로 무시
    ReadStatus toEntity(ReadStatusDto dto);
}
