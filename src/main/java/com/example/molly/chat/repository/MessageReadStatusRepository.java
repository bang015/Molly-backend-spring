package com.example.molly.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.molly.chat.entity.ChatRoom;
import com.example.molly.chat.entity.MessageReadStatus;
import com.example.molly.user.entity.User;

public interface MessageReadStatusRepository extends JpaRepository<MessageReadStatus, Long> {
  @Query("SELECT COUNT(mrs) FROM MessageReadStatus mrs " +
      "WHERE mrs.message.room = :room " +
      "AND mrs.user = :user " +
      "AND mrs.isRead = false")
  int countUnreadMessageByRoom(@Param("room") ChatRoom room, @Param("user") User user);

  @Query("SELECT COUNT(mrs) FROM MessageReadStatus mrs " +
      "WHERE mrs.user = :user " +
      "AND mrs.isRead = false")
  int countUnreadMessageByUser(@Param("user") User user);
}
