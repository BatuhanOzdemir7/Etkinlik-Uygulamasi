package com.works.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.works.CacheTestConfig;
import com.works.dto.EventCreateRequestDto;
import com.works.dto.UserResponseDto; // Sahte kullanıcı için eklenen import
import com.works.entity.Event;
import com.works.service.EventService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession; // Sahte oturum için eklenen import
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(CacheTestConfig.class)
@WebMvcTest(EventRestController.class)
class EventRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreate_Success() throws Exception {
        // Arrange
        EventCreateRequestDto requestDto = new EventCreateRequestDto();
        requestDto.setTitle("Bahar Şenliği");
        requestDto.setDescription("Üniversite bahar şenliği organizasyonu");
        // Eksik olan zorunlu validasyon alanları dolduruluyor
        requestDto.setEventDate(LocalDate.now().plusDays(5)); // Gelecek bir tarih
        requestDto.setEventTime(LocalTime.of(14, 0));
        requestDto.setLocation("Mete Cengiz Kültür Merkezi");
        requestDto.setCategory("Kültür-Sanat");

        Event mockEvent = new Event();
        mockEvent.setId(100);
        mockEvent.setTitle("Bahar Şenliği");

        Mockito.when(eventService.create(any(EventCreateRequestDto.class)))
                .thenReturn(mockEvent);

        // SessionFilter'ı geçebilmek için sahte oturum verisi oluşturuluyor
        MockHttpSession mockSession = new MockHttpSession();
        UserResponseDto sessionUser = new UserResponseDto();
        sessionUser.setId(1L);
        mockSession.setAttribute("user", sessionUser);

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/event/create")
                        .session(mockSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.title").value("Bahar Şenliği"));
    }

    @Test
    void testDeleteOne_Success() throws Exception {
        // Arrange
        ResponseEntity<Object> mockResponse = ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "Event deleted successfully."
        ));

        Mockito.when(eventService.deleteOne(anyLong()))
                .thenReturn(mockResponse);

        // SessionFilter'ı geçebilmek için sahte oturum verisi oluşturuluyor
        MockHttpSession mockSession = new MockHttpSession();
        UserResponseDto sessionUser = new UserResponseDto();
        sessionUser.setId(1L);
        mockSession.setAttribute("user", sessionUser);

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/event/deleteOne/100")
                        .session(mockSession)) // Sahte oturum isteğe enjekte edildi
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Event deleted successfully."));
    }

    @Test
    void testDeleteOne_Unauthorized_WhenNoSession() throws Exception {
        // Arrange - Herhangi bir session veya kullanıcı bilgisi VERMİYORUZ.
        // Sisteme sızmaya çalışan anonim bir istek taklit ediliyor.

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/event/deleteOne/100"))
                .andExpect(status().isUnauthorized()) // HTTP 401 Unauthorized BEKLİYORUZ
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Unauthorized access. Please log in."));
    }

    @Test
    void testJoinEvent_Success() throws Exception { //oturum açmış kişi etkinliğe katılabiliyor mu
        // Arrange
        ResponseEntity<Object> mockResponse = ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "Etkinliğe başarıyla katıldınız."
        ));

        Mockito.when(eventService.joinEvent(100L)).thenReturn(mockResponse);

        MockHttpSession mockSession = new MockHttpSession();
        UserResponseDto sessionUser = new UserResponseDto();
        sessionUser.setId(1L);
        mockSession.setAttribute("user", sessionUser);

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/event/join/100")
                        .session(mockSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Etkinliğe başarıyla katıldınız."));
    }

    @Test
    void testJoinEvent_AlreadyJoined() throws Exception { //oturum açmış, etkinliğe katılmış bi daha katılabilir mi
        // Arrange
        ResponseEntity<Object> mockResponse = ResponseEntity.status(400).body(Map.of(
                "success", false,
                "message", "Bu etkinliğe zaten katıldınız."
        ));

        Mockito.when(eventService.joinEvent(100L)).thenReturn(mockResponse);

        MockHttpSession mockSession = new MockHttpSession();
        UserResponseDto sessionUser = new UserResponseDto();
        sessionUser.setId(1L);
        mockSession.setAttribute("user", sessionUser);

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/event/join/100")
                        .session(mockSession))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Bu etkinliğe zaten katıldınız."));
    }

    @Test
    void testLeaveEvent_Success() throws Exception { //oturum açmış, etkinliğe kayıtlı ama çıkmak istiyor
        // Arrange
        ResponseEntity<Object> mockResponse = ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "Etkinlik katılımınız iptal edildi."
        ));

        Mockito.when(eventService.leaveEvent(100L)).thenReturn(mockResponse);

        MockHttpSession mockSession = new MockHttpSession();
        UserResponseDto sessionUser = new UserResponseDto();
        sessionUser.setId(1L);
        mockSession.setAttribute("user", sessionUser);

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/event/leave/100")
                        .session(mockSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Etkinlik katılımınız iptal edildi."));
    }

    @Test
    void testLeaveEvent_NotJoined() throws Exception { //oturum açmış, etkinliğe kayıtlı değil ama çıkmak istiyor
        // Arrange
        ResponseEntity<Object> mockResponse = ResponseEntity.status(400).body(Map.of(
                "success", false,
                "message", "Bu etkinliğe zaten kayıtlı değilsiniz."
        ));

        Mockito.when(eventService.leaveEvent(100L)).thenReturn(mockResponse);

        MockHttpSession mockSession = new MockHttpSession();
        UserResponseDto sessionUser = new UserResponseDto();
        sessionUser.setId(1L);
        mockSession.setAttribute("user", sessionUser);

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/event/leave/100")
                        .session(mockSession))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Bu etkinliğe zaten kayıtlı değilsiniz."));
    }

    @Test
    void testCreate_ValidationError_MissingFields() throws Exception {
        // Arrange - Zorunlu alanları (title, location, category vb.) tamamen boş bir DTO bırakıyoruz
        com.works.dto.EventCreateRequestDto invalidRequestDto = new com.works.dto.EventCreateRequestDto();

        // SessionFilter'ı geçebilmek için geçerli bir oturum sağlıyoruz
        MockHttpSession mockSession = new MockHttpSession();
        UserResponseDto sessionUser = new UserResponseDto();
        sessionUser.setId(1L);
        mockSession.setAttribute("user", sessionUser);

        // Act and Assert
        // Eksik veri gönderildiği için sistemin HTTP 400 Bad Request dönmesini bekliyoruz
        mockMvc.perform(MockMvcRequestBuilders.post("/event/create")
                        .session(mockSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isArray()) // Hata mesajlarının bir dizi olarak döndüğünü doğrular
                .andExpect(jsonPath("$[0].field").exists())
                .andExpect(jsonPath("$[0].message").exists());
    }

    @Test
    void testCreate_ValidationError_PastDate() throws Exception {
        // Arrange - Geçmiş bir tarih verilerek validasyon hatası tetikleniyor
        com.works.dto.EventCreateRequestDto pastDateRequestDto = new com.works.dto.EventCreateRequestDto();
        pastDateRequestDto.setTitle("Geçmiş Etkinlik");
        pastDateRequestDto.setDescription("Açıklama");
        pastDateRequestDto.setEventDate(java.time.LocalDate.now().minusDays(5)); // Geçmiş tarih
        pastDateRequestDto.setEventTime(java.time.LocalTime.of(12, 0));
        pastDateRequestDto.setLocation("Bursa");
        pastDateRequestDto.setCategory("Eğitim");

        MockHttpSession mockSession = new MockHttpSession();
        UserResponseDto sessionUser = new UserResponseDto();
        sessionUser.setId(1L);
        mockSession.setAttribute("user", sessionUser);

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/event/create")
                        .session(mockSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pastDateRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetParticipants_Success() throws Exception {
        // Arrange
        ResponseEntity<Object> mockResponse = ResponseEntity.ok().body(Map.of(
                "success", true,
                "participants", new java.util.HashSet<>() // Boş liste taklidi yapıyoruz
        ));

        Mockito.when(eventService.getParticipants(100L)).thenReturn(mockResponse);

        MockHttpSession mockSession = new MockHttpSession();
        UserResponseDto sessionUser = new UserResponseDto();
        sessionUser.setId(1L);
        mockSession.setAttribute("user", sessionUser);

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/event/100/participants")
                        .session(mockSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.participants").exists());
    }
}

