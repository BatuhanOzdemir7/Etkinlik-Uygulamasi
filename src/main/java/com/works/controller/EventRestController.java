package com.works.controller;

import com.works.dto.EventCreateRequestDto;
import com.works.dto.EventUpdateRequestDto;
import com.works.entity.Event;
import com.works.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

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

    @DeleteMapping("deleteOne/{id}")
    public ResponseEntity deleteOne(@PathVariable Long id){
        return eventService.deleteOne(id);
    }

    @PutMapping("update")
    public ResponseEntity update(@Valid @RequestBody EventUpdateRequestDto eventUpdateRequestDto){
        return eventService.update(eventUpdateRequestDto);
    }

    @GetMapping("list")
    public Page<Event> eventList(
            @RequestParam(defaultValue = "0") int page
    ){
        return eventService.eventList(page);
    }

    @GetMapping("search")
    public Page<Event> search(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "asc") String sortDir
    ){
        return eventService.search(q, page, sortDir);
    }

}