package com.example.molly.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.molly.chat.entity.ChatRoom;
import com.example.molly.chat.entity.MessageReadStatus;

public interface MessageReadStatusRepository extends JpaRepository<MessageReadStatus, Long> {
  @Query("SELECT COUNT(mrs) FROM MessageReadStatus mrs" +
      "WHERE mrs.message.room = :room" +
      "AND mrs.isRead = false")
  int countUnreadMessageByRoom(@Param("room") ChatRoom room);
}
