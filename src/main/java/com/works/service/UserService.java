package com.works.service;

import com.works.entity.User;
import com.works.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.mindrot.jbcrypt.BCrypt;
import org.modelmapper.ModelMapper;
import com.works.dto.UserRegisterRequestDto;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    final UserRepository UserRepository;
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
        UserRepository.save(user);
        return ResponseEntity.ok().body(user);
    }
}