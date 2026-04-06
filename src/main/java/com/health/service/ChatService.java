package com.health.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.health.entity.Chat;
import com.health.repository.ChatRepository;

import jakarta.transaction.Transactional;

@Service
public class ChatService {
	
	@Autowired
	private ChatRepository chatRepo;

	public void saveMessage(Long chatId,Long userId, String sender,String message) {
		Chat chat = new Chat();
		chat.setChatId(chatId);
		chat.setUserId(userId);
		chat.setSender(sender);
		chat.setMessage(message);
		List<Chat> existing = chatRepo.findByChatIdAndUserId(chatId, userId);
		if(existing.isEmpty() && sender.equals("You")) {
			chat.setTitle(message);
		}else {
			chat.setTitle(existing.get(0).getTitle());
		}
		chatRepo.save(chat);
	}
	
	// get chat history...
	public List<Chat> getChat(Long chatId,Long userId){
		return chatRepo.findByChatIdAndUserId(chatId,userId);
	}
	
	// delete chat....
	@Transactional
	public void deleteChat(Long chatId,Long userId) {
		chatRepo.deleteByChatIdAndUserId(chatId,userId);
	}
	
	public List<Long> getAllChatsIds(Long userId){
		return chatRepo.findChatIdsByUserId(userId);
	}
	
	public List<Object[]> getChatList(Long userId){
		return chatRepo.findChatIdAndTitleByUserId(userId);
	}
}
