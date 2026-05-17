package com.works.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import jakarta.validation.constraints.FutureOrPresent;

@Data
public class EventCreateRequestDto {

    @NotNull
    @NotEmpty
    private String title;

    @NotNull
    @NotEmpty
    private String description;

    @NotNull(message = "Etkinlik tarihi boş bırakılamaz")
    @FutureOrPresent(message = "Etkinlik tarihi geçmiş bir tarih olamaz")
    private LocalDate eventDate;

    @NotNull
    private LocalTime eventTime;

    @NotNull
    @NotEmpty
    private String location;

    @NotNull
    @NotEmpty
    private String category;
}