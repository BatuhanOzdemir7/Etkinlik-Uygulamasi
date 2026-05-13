package com.works.controller;

import com.works.dto.UserRegisterRequestDto;
import com.works.entity.User;
import com.works.dto.UserLoginRequestDto;
import com.works.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserRestController {

    final UserService UserService;

    @PostMapping("/register")
    public ResponseEntity register(@Valid @RequestBody UserRegisterRequestDto userRegisterRequestDto){
        return UserService.register(userRegisterRequestDto);
    }

    @PostMapping("/login")
    public ResponseEntity login(@Valid @RequestBody UserLoginRequestDto userLoginRequestDto){
        return UserService.login(userLoginRequestDto);
    }
}