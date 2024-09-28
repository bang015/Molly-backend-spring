package com.example.molly.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import com.example.molly.chat.entity.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
  @Query("SELECT c FROM ChatRoom c JOIN FETCH c.messages WHERE c.id = :roomId")
  Optional<ChatRoom> findChatRoomWithMessages(@Param("roomId") Long roomId);
}
