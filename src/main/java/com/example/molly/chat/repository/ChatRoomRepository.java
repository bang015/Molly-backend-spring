package com.example.molly.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.molly.chat.entity.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

}
