package com.works.dto;

import jakarta.validation.constraints.*;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link com.works.entity.Event}
 */
@Value
public class EventUpdateRequestDto implements Serializable {
    @NotNull
    @Min(1)
    @Max(Long.MAX_VALUE)
    Long id;
    @NotNull
    @Size(min = 2, max = 100)
    @NotEmpty
    String title;
    @NotNull
    @Size(min = 2, max = 500)
    @NotEmpty
    String description;
}