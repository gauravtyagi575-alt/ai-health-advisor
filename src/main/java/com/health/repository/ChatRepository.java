package com.health.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.health.entity.Chat;

public interface ChatRepository extends JpaRepository<Chat, Long> {
	
	List<Chat> findByChatIdAndUserId(Long chatId,Long userId);
	void deleteByChatIdAndUserId(Long chatId,Long userId);
	@Query("SELECT DISTINCT c.chatId FROM Chat c WHERE c.userId = ?1 ORDER BY c.chatId DESC")
	List<Long> findChatIdsByUserId(Long userId);
	
	@Query("SELECT c.chatId, c.title FROM Chat c WHERE c.userId = ?1 GROUP BY c.chatId, c.title ORDER BY c.chatId DESC")
	List<Object[]> findChatIdAndTitleByUserId(Long userId);
}
