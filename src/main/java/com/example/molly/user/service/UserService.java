package com.example.molly.user.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.molly.user.entity.User;
import com.example.molly.user.repository.UserRepository;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  public User isEmailDuplicate(String email) {
    Optional<User> user = userRepository.findByEmail(email);
    return user.orElse(null);
  }

  public User isNicknameDuplicate(String nickname) {
    Optional<User> user = userRepository.findByNickname(nickname);
    return user.orElse(null);
  }
}
