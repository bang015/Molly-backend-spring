package com.example.molly.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import com.example.molly.chat.entity.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
  @Query("SELECT c FROM ChatRoom c JOIN FETCH c.users cm JOIN FETCH cm.user u WHERE c.id = :roomId")
  Optional<ChatRoom> findChatRoomWithUsers(@Param("roomId") Long roomId);

  @Query("SELECT r FROM ChatRoom r JOIN r.users m1 JOIN r.users m2 " +
      "WHERE m1.user.id = :userId AND m2.user.id = :memberId AND r.isGroupChat = false")
  Optional<ChatRoom> findPrivateRoom(@Param("userId") Long userId, @Param("memberId") Long memberId);
}
