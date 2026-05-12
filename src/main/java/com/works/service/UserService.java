package com.works.service;

import com.works.entity.User;
import com.works.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    final UserRepository UserRepository;


}