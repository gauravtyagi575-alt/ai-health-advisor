package com.health.service;




import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AiService {

	@Value("${gemini.api.key}")
   private String apiKey;
	
	 @Value("${gemini.api.url}")
	private String apiURL;
	
	public String getResponse(String question) {
		RestTemplate restTemplete = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		String prompt = "You are a fitness trainer. Answer shortly : "+question;
		
		JSONObject textPart = new JSONObject();
		textPart.put("text", prompt);
		
		JSONArray partsArray = new JSONArray();
		partsArray.put(textPart);
		
		JSONObject content = new JSONObject();
		content.put("parts", partsArray);
		
		JSONArray contentArray = new JSONArray();
		contentArray.put(content);
		
		JSONObject body = new JSONObject();
		body.put("contents", contentArray);
		
		HttpEntity<String> request = new HttpEntity<>(body.toString(),headers);
		String fullUrl = apiURL + "?key=" + apiKey;
		
		try {
			String response = restTemplete.postForObject(fullUrl, request, String.class);
			JSONObject json = new JSONObject(response);
			
			  return json.getJSONArray("candidates")
		                .getJSONObject(0)
		                .getJSONObject("content")
		                .getJSONArray("parts")
		                .getJSONObject(0)
		                .getString("text");
		} catch (Exception e) {
		    return "⚠️ AI limit reached. Please try again later.";
		}
		
	
	}
}
