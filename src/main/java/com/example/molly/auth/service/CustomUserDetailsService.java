package com.example.molly.auth.service;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.molly.auth.dto.CustomUserDetails;
import com.example.molly.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    com.example.molly.user.entity.User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    return User.builder().username(user.getId().toString()).password(user.getPassword()).build();
  }

  public UserDetails loadUserById(Long id) throws UsernameNotFoundException {
    com.example.molly.user.entity.User user = userRepository.findById(id)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
    List<GrantedAuthority> authorities = List.of();
    return new CustomUserDetails(
        user.getId().toString(),
        user.getPassword(),
        authorities);
  }
}
