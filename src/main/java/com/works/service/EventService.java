package com.works.service;

import com.works.dto.EventCreateRequestDto;
import com.works.entity.Event;
import com.works.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    final EventRepository eventRepository;
    ModelMapper modelMapper = new ModelMapper();

    public Event create(EventCreateRequestDto eventCreateRequestDto) {
        Event event = modelMapper.map(eventCreateRequestDto, Event.class);
        return eventRepository.save(event);
    }

    public List<Event> createAll(List<EventCreateRequestDto> eventCreateRequestDtos){
        List<Event> eventList = eventCreateRequestDtos.stream()
                .map(dto -> modelMapper.map(dto, Event.class))
                .toList();
        return eventRepository.saveAll(eventList);
    }

}