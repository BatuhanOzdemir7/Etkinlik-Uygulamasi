package com.works.dto;

import jakarta.validation.constraints.*;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

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
    @NotEmpty
    String title;

    @NotNull
    @NotEmpty
    String description;

    @NotNull
    LocalDate eventDate;

    @NotNull
    LocalTime eventTime;

    @NotNull
    @NotEmpty
    String location;

    @NotNull
    @NotEmpty
    String category;
}