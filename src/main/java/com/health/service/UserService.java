package com.health.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.health.entity.User;
import com.health.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepo;
	
	public void registerUser(User user) {
		userRepo.save(user);
	}
	
	public User login(String username,String password) {
		User user = userRepo.findByUsername(username);
		if(user != null && user.getPassword().equals(password)) {
			return user;
		}
		return null;
	}
}
