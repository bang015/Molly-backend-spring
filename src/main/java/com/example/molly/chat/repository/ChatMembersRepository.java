package com.example.molly.chat.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.molly.chat.entity.ChatMembers;
import com.example.molly.chat.entity.ChatRoom;
import com.example.molly.user.entity.User;

public interface ChatMembersRepository extends JpaRepository<ChatMembers, Long> {
  @Query("SELECT cm.room FROM ChatMembers cm WHERE cm.user = :user")
  Page<ChatRoom> findChatRoomsByUser(@Param("user") User user, Pageable pageable);

  @Query("SELECT cm.user FROM ChatMembers cm WHERE cm.room = :room")
  List<User> findMembersByRoom(@Param("room") ChatRoom room);
}
