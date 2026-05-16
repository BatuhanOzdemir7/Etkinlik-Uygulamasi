package com.works.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(name = "event_date")
    @JdbcTypeCode(SqlTypes.DATE)
    private LocalDate eventDate;

    @Column(name = "event_time")
    @JdbcTypeCode(SqlTypes.TIME)
    private LocalTime eventTime;

    @Column(name = "location")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String location;

    @Column(name = "category")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String category;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

}