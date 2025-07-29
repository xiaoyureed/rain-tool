package org.eu.rainx0.raintool.core.starter.data.jpa.converter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * db: timestamp
 * entity: LocalDateTime
 *
 * @author: xiaoyu
 * @time: 2025/6/30 20:49
 */

@Converter(autoApply = false) // 需要在 entity 字段上手动标注 @Convert
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, Timestamp> {
    @Override
    public Timestamp convertToDatabaseColumn(LocalDateTime attribute) {
        return Optional.ofNullable(attribute).map(Timestamp::valueOf).orElse(null);
    }

    @Override
    public LocalDateTime convertToEntityAttribute(Timestamp dbData) {
        return Optional.ofNullable(dbData).map(Timestamp::toLocalDateTime).orElse(null);
    }
}
