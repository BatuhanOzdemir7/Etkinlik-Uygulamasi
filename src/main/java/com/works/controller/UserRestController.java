package com.works.controller;

import com.works.dto.UserRegisterRequestDto;
import com.works.entity.User;
import com.works.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserRestController {

    final UserService UserService;

    @PostMapping("/register")
    public ResponseEntity register(@Valid @RequestBody UserRegisterRequestDto customerRegisterRequestDto){
        return UserService.register(customerRegisterRequestDto);
    }

}