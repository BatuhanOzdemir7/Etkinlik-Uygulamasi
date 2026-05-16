package com.works.service;

import com.works.entity.User;
import com.works.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.mindrot.jbcrypt.BCrypt;
import org.modelmapper.ModelMapper;
import com.works.dto.UserRegisterRequestDto;
import com.works.dto.UserLoginRequestDto;
import com.works.dto.UserResponseDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    final UserRepository UserRepository;
    final HttpServletRequest request;
    ModelMapper modelMapper = new ModelMapper();

    public ResponseEntity register(UserRegisterRequestDto userRegisterRequestDto) {
        List<User> UserList = UserRepository.findByEmailEqualsOrPhoneEqualsAllIgnoreCase(userRegisterRequestDto.getEmail(), userRegisterRequestDto.getPhone());
        if (UserList.size() > 0) {
            // daha önceden bu email veya phone kullanılmış demektir.
            Map<String, Object> hm = Map.of("success", false, "message", "This email or phone number is already in use.");
            return ResponseEntity.badRequest().body(hm);
        }
        User user = modelMapper.map(userRegisterRequestDto, User.class);
        String hashPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashPassword);
        user.setEnabled(true);
        /*
        Projenin geliştirme aşamasında, temel fonksiyonların ve uçtan uca akışların hızlıca test edilebilmesi adına kullanıcı aktivasyon süreci geçici olarak otomatikleştirilmiştir.
        Bu doğrultuda, UserService katmanında yapılan düzenlemeyle yeni kayıt olan tüm kullanıcıların enabled durumu varsayılan olarak true değerine atanmaktadır. Bu yaklaşım, e-posta
        doğrulama gibi dış servislerin henüz entegre edilmediği bu fazda geliştirme verimliliğini artırmaktadır. Güvenlik protokolleri tamamlandığında, sistem gerçek senaryolara uygun olan
        'onaylı kayıt' modeline geri çekilecektir.
         */
        UserRepository.save(user);
        return ResponseEntity.ok().body(user);
    }

    // login
    public ResponseEntity login(UserLoginRequestDto UserLoginRequestDto){
        Optional<User> optionalUser = UserRepository.findByEnabledTrueAndEmailIgnoreCaseOrEnabledTrueAndPhoneIgnoreCase(UserLoginRequestDto.getUsername(), UserLoginRequestDto.getUsername());
        if(optionalUser.isPresent()){
            User User = optionalUser.get();
            boolean isMatch = BCrypt.checkpw(UserLoginRequestDto.getPassword(), User.getPassword());
            if(isMatch){
                UserResponseDto userResponseDto = modelMapper.map(User, UserResponseDto.class);
                request.getSession().setAttribute("user", userResponseDto);
                return ResponseEntity.ok().body(userResponseDto);
            }
        }
        Map<String, Object> hm = Map.of("success", false, "message", "Username or password is incorrect.");
        return ResponseEntity.badRequest().body(hm);
    }

    // logout
    public ResponseEntity<Object> logout() {
        // Mevcut oturumu getir, yoksa yeni bir oturum oluşturma (false parametresi)
        jakarta.servlet.http.HttpSession session = request.getSession(false);

        if (session != null) {
            // Oturumu ve bellekteki "user" gibi tüm verileri tamamen yok et
            session.invalidate();
        }

        Map<String, Object> hm = Map.of("success", true, "message", "Başarıyla çıkış yapıldı.");
        return ResponseEntity.ok().body(hm);
    }
}