package com.works.service;

import com.works.dto.EventCreateRequestDto;
import com.works.dto.EventUpdateRequestDto;
import com.works.entity.Event;
import com.works.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    public ResponseEntity deleteOne(Long id) {
        Optional<Event> optionalEvent = eventRepository.findById(id);
        if(optionalEvent.isPresent()){
            eventRepository.deleteById(id);
            Map<String, Object> hm = Map.of("success", true, "message", "Event deleted successfully.");
            return ResponseEntity.ok().body(hm);
        }else {
            Map<String, Object> hm = Map.of("success", false, "message", "Event not found id: " + id + "");
            return ResponseEntity.status(404).body(hm);
        }
    }

    public ResponseEntity update(EventUpdateRequestDto eventUpdateRequestDto) {
        Optional<Event> optionalEvent = eventRepository.findById(eventUpdateRequestDto.getId());
        if(optionalEvent.isPresent()){
            Event event = modelMapper.map(eventUpdateRequestDto, Event.class);
            eventRepository.save(event);
            Map<String, Object> hm = Map.of("success", true, "message", "Event updated successfully.");
            return ResponseEntity.ok().body(hm);
        }else {
            Map<String, Object> hm = Map.of("success", false, "message", "Event not found id: " + eventUpdateRequestDto.getId() + "");
            return ResponseEntity.status(404).body(hm);
        }
    }

    public Page<Event> eventList(int page){
        Pageable pageable = Pageable.ofSize(10).withPage(page);
        return eventRepository.findAll(pageable);
    }

    public Page<Event> search(String q, int page, String sortDir){
        Sort sort = Sort.by(sortDir.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, "eventDate");
        Pageable pageable = PageRequest.of(page, 10, sort);
        Page<Event> eventPage = eventRepository.findByTitleContainsOrDescriptionContainsAllIgnoreCase(q, q, pageable);
        return eventPage;
    }

}