package com.example.molly.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.molly.user.entity.ProfileImage;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {

}
