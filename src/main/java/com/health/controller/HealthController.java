package com.health.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.health.AiPoweredHealthAdvisorSystemApplication;
import com.health.entity.User;
import com.health.entity.UserData;
import com.health.service.AiService;
import com.health.service.ChatService;
import com.health.service.HealthService;
import com.health.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class HealthController {

    private final AiPoweredHealthAdvisorSystemApplication aiPoweredHealthAdvisorSystemApplication;

	@Autowired
	private HealthService healthService;
	
	@Autowired
	private ChatService chatService;
	
	@Autowired
	private AiService aiService;
	
	@Autowired
	private UserService userService;

    HealthController(AiPoweredHealthAdvisorSystemApplication aiPoweredHealthAdvisorSystemApplication) {
        this.aiPoweredHealthAdvisorSystemApplication = aiPoweredHealthAdvisorSystemApplication;
    }
	
	@GetMapping("/")
	public String home() {
		return "index";
	}
	
	@PostMapping("/calculate")
	public String calcuate(@ModelAttribute UserData data, Model model){

		double bmi = healthService.calculateBMI(data.getHeigth(), data.getWeigth());
		String category = healthService.getBMICategory(bmi);
		String aiResponse = aiService.getResponse("My BMI is "+bmi+" and  I am "+category+". Give short diet and exercise advice.");
		
		model.addAttribute("bmi", bmi);
		model.addAttribute("aiAdvice",aiResponse);
		model.addAttribute("category",category);
		return "result";
		
	}
	@GetMapping("/chat")
	public String chatPage(HttpSession session) {
		User user = (User)session.getAttribute("user");
		if(user == null) {
			return "redirect:/login";
		}
		return "chat";
	}
	
	@PostMapping("/ask")
	public String ask(@RequestParam("question") String question, Model model,HttpSession session) {
		
		User user = (User)session.getAttribute("user");
		if(user == null) {
			return "redirect:/login";
		}
		if(question != null && !question.isEmpty()) {
			String response = aiService.getResponse(question);
			response = response.replace("**", "");
			// chatId store in session...
			Long chatId = (Long)session.getAttribute("chatId");
			
			if(chatId == null) {
				chatId = System.currentTimeMillis();  // uniq id
				session.setAttribute("chatId", chatId);
			}
			//db save
			chatService.saveMessage(chatId,user.getId(), "You", question);
			chatService.saveMessage(chatId,user.getId(), "Ai", response);
		}
		// chat load from db
		Long chatId = (Long)session.getAttribute("chatId");
		
		if(chatId != null) {
			model.addAttribute("chatHistory",chatService.getChat(chatId,user.getId()));
		}
		
		return "chat";
	}
	
	@GetMapping("/new")
	public String newChat(HttpSession session) {
		session.setAttribute("chatId", System.currentTimeMillis());
		return "chat";
	}
	
	@GetMapping("/clear")
	public String clearChat(HttpSession session) {
		User user = (User)session.getAttribute("user");
		Long chatId = (Long)session.getAttribute("chatId");
		if(chatId != null && user != null) {
			chatService.deleteChat(chatId,user.getId());
		}
		return "chat";
	}
	@GetMapping("/chatList")
	public String chatList(Model model,HttpSession session) {
		User user = (User)session.getAttribute("user");
		model.addAttribute("chatIds", chatService.getAllChatsIds(user.getId()));
		return "chat-list";
	}
	
	@GetMapping("/loadChat/{id}")
	public String loadChat(@PathVariable Long id,HttpSession session,Model model) {
		User user = (User)session.getAttribute("user");
		session.setAttribute("chatId", id);
		model.addAttribute("chatHistory", chatService.getChat(id,user.getId()));
		return "chat";
	}
	
	@GetMapping("/deleteChat/{id}")
	public String deleteChat(@PathVariable Long id,HttpSession session) {
		User user = (User)session.getAttribute("user");
		chatService.deleteChat(id,user.getId());
		return "redirect:/chatList";
	}
	@GetMapping("/signup")
	public String signupPage() {
		return "signup";
	}
	
	@PostMapping("/signup")
	public String signup(User user) {
		userService.registerUser(user);
		return "redirect:/login";
	}
	
	@GetMapping("/login")
	public String loginPage() {
		return "login";
	}
	
	@PostMapping("/login")
	public String login(@RequestParam String username,@RequestParam String password, HttpSession session) {
		User user = userService.login(username, password);
		if(user != null) {
			session.setAttribute("user", user);
			return "redirect:/chat";
		}
		return "login";
	}
	
	@GetMapping("/logout")
	public String logout(HttpSession session) {

	    session.invalidate(); 

	    return "redirect:/login";
	}
	
}
