package com.works.controller;

import com.works.dto.EventCreateRequestDto;
import com.works.entity.Event;
import com.works.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("event")
@RequiredArgsConstructor
public class EventRestController {

    final EventService eventService;

    @PostMapping("create")
    public Event create(@Valid @RequestBody EventCreateRequestDto eventCreateRequestDto){
        return eventService.create(eventCreateRequestDto);
    }

    @PostMapping("createAll")
    public List<Event> createAll(@Valid @RequestBody List<EventCreateRequestDto> eventCreateRequestDto){
        return eventService.createAll(eventCreateRequestDto);
    }

}