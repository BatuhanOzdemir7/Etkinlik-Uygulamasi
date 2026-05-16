package com.works.service;

import com.works.dto.EventCreateRequestDto;
import com.works.dto.EventUpdateRequestDto;
import com.works.dto.UserResponseDto;
import com.works.entity.Event;
import com.works.entity.User;
import com.works.repository.EventRepository;
import com.works.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventService {

    final EventRepository eventRepository;

    private final UserRepository userRepository;
    private final HttpServletRequest request;
    ModelMapper modelMapper = new ModelMapper();

    public Event create(EventCreateRequestDto eventCreateRequestDto) {
        //DTO'yu normal Event nesnesine çeviriyoruz
        Event event = modelMapper.map(eventCreateRequestDto, Event.class);
        //Sisteme giriş yapmış olan kullanıcının bilgilerini Session'dan çekiyoruz
        com.works.dto.UserResponseDto sessionUser = (com.works.dto.UserResponseDto) request.getSession().getAttribute("user");
        // 3. Optional ile kullanıcının veritabanında hala var olduğundan emin oluyoruz (cid üzerinden)
        Optional<com.works.entity.User> optionalUser = userRepository.findById(sessionUser.getCid());
        if (optionalUser.isPresent()) {
            // 4. Veritabanından gelen User varlığını, Etkinliğin sahibi olarak atıyoruz
            event.setOwner(optionalUser.get());
            // 5. Kullanıcıyı varsayılan olarak etkinliğin ilk katılımcısı listesine ekle
            event.getParticipants().add(optionalUser.get());
        }
        return eventRepository.save(event);
    }

    public List<Event> createAll(List<EventCreateRequestDto> eventCreateRequestDtos){
        List<Event> eventList = eventCreateRequestDtos.stream()
                .map(dto -> modelMapper.map(dto, Event.class))
                .toList();
        return eventRepository.saveAll(eventList);
    }

    public ResponseEntity deleteOne(Long id) {
        //işlemi yapan aktif kullanıcıyı oturumdan alıyoruz
        com.works.dto.UserResponseDto sessionUser = (com.works.dto.UserResponseDto) request.getSession().getAttribute("user");

        Optional<Event> optionalEvent = eventRepository.findById(id);
        if(optionalEvent.isPresent()){
            //veri tabanında var olduğunu doğruladığımız kaydı Optional koruyucu kutusundan çıkarıp gerçek bir Event nesnesine dönüştürür.
            Event event = optionalEvent.get();
            //güvenlik Kontrolü: İşlemi yapan kişinin ID'si ile etkinliği oluşturanın ID'si eşleşiyor mu?
            if (!event.getOwner().getCid().equals(sessionUser.getCid())) {
                Map<String, Object> hm = Map.of("success", false, "message", "Bu etkinliği silme yetkiniz bulunmuyor.");
                return ResponseEntity.status(403).body(hm); // 403 Yasaklandı hatası dönüyoruz
            }
            //kontrolden geçerse normal silme işlemine devam et
            eventRepository.deleteById(id);
            Map<String, Object> hm = Map.of("success", true, "message", "Event deleted successfully.");
            return ResponseEntity.ok().body(hm);
        }else {
            Map<String, Object> hm = Map.of("success", false, "message", "Event not found id: " + id + "");
            return ResponseEntity.status(404).body(hm);
        }
    }

    public ResponseEntity update(EventUpdateRequestDto eventUpdateRequestDto) {
        //işlemi yapan aktif kullanıcıyı oturumdan alıyoruz
        com.works.dto.UserResponseDto sessionUser = (com.works.dto.UserResponseDto) request.getSession().getAttribute("user");
        Optional<Event> optionalEvent = eventRepository.findById(eventUpdateRequestDto.getId());
        if(optionalEvent.isPresent()){
            Event existingEvent = optionalEvent.get();

            //güvenlik Kontrolü: İşlemi yapan kişinin ID'si ile etkinliği oluşturanın ID'si eşleşiyor mu?
            if (!existingEvent.getOwner().getCid().equals(sessionUser.getCid())) {
                Map<String, Object> hm = Map.of("success", false, "message", "Bu etkinliği güncelleme yetkiniz bulunmuyor.");
                return ResponseEntity.status(403).body(hm); // 403 Yasaklandı hatası dönüyoruz
            }
            //kontrolden geçerse verileri kopyala
            Event event = modelMapper.map(eventUpdateRequestDto, Event.class);
            //kritik Adım: Etkinliğin asıl sahibini (owner) yeni nesneye aktar, aksi halde null olur
            event.setOwner(existingEvent.getOwner());
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

    public ResponseEntity<Object> joinEvent(Long eventId) {
        UserResponseDto sessionUser = (UserResponseDto) request.getSession().getAttribute("user");
        Optional<Event> optionalEvent = eventRepository.findById(eventId);

        if (optionalEvent.isPresent()) {
            Event event = optionalEvent.get();
            User user = userRepository.findById(sessionUser.getCid()).get();

            if (event.getParticipants().contains(user)) {
                return ResponseEntity.<Object>status(400).body(Map.of("success", false, "message", "Bu etkinliğe zaten katıldınız."));
            }

            event.getParticipants().add(user);
            eventRepository.save(event);
            return ResponseEntity.<Object>ok(Map.of("success", true, "message", "Etkinliğe başarıyla katıldınız."));
        }

        return ResponseEntity.<Object>status(404).body(Map.of("success", false, "message", "Etkinlik bulunamadı."));
    }

    public ResponseEntity<Object> leaveEvent(Long eventId) {
        UserResponseDto sessionUser = (UserResponseDto) request.getSession().getAttribute("user");
        Optional<Event> optionalEvent = eventRepository.findById(eventId);

        if (optionalEvent.isPresent()) {
            Event event = optionalEvent.get();
            User user = userRepository.findById(sessionUser.getCid()).get();

            if (!event.getParticipants().contains(user)) {
                return ResponseEntity.<Object>status(400).body(Map.of("success", false, "message", "Bu etkinliğe zaten kayıtlı değilsiniz."));
            }

            event.getParticipants().remove(user);
            eventRepository.save(event);
            return ResponseEntity.<Object>ok(Map.of("success", true, "message", "Etkinlik katılımınız iptal edildi."));
        }

        return ResponseEntity.<Object>status(404).body(Map.of("success", false, "message", "Etkinlik bulunamadı."));
    }

    public ResponseEntity<Object> getEventDetail(Long id) {
        return eventRepository.findById(id).map(event ->
                ResponseEntity.<Object>ok(Map.of("success", true, "event", event))
        ).orElseGet(() ->
                ResponseEntity.<Object>status(404).body(Map.of("success", false, "message", "Etkinlik bulunamadı."))
        );
    }

}