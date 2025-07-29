package org.eu.rainx0.raintool.core.starter.data.jpa.converter;

import java.util.Optional;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * @author: xiaoyu
 * @time: 2025/7/9 16:38
 */
@Converter(autoApply = true)
public class BooleanIntegerConverter implements AttributeConverter<Boolean, Integer> {
    @Override
    public Integer convertToDatabaseColumn(Boolean attribute) {
        return Optional.ofNullable(attribute)
            .map(ele -> ele ? 1 : 0)
            .orElse(null);
    }

    @Override
    public Boolean convertToEntityAttribute(Integer dbData) {
        return Optional.ofNullable(dbData)
            .map(ele -> ele == 1)
            .orElse(null);
    }
}
