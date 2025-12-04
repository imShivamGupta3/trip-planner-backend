package com.Shivam.tripplanner.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final Gson gson = new Gson();

    public String generateTripPlan(String destination, int days, String budget, String travelers) {
        String prompt = buildPrompt(destination, days, budget, travelers);
        String url = apiUrl + "?key=" + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(part));

        requestBody.put("contents", List.of(content));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);
            return extractAndCleanResponse(response);
        } catch (Exception e) {
            throw new RuntimeException("Error calling Gemini API: " + e.getMessage());
        }
    }

    private String buildPrompt(String destination, int days, String budget, String travelers) {
        return String.format("""
            Generate a travel itinerary for %s for %d days for a %s trip with a %s budget.
            
            CRITICAL: Output MUST be valid JSON only. No markdown, no backticks, no preamble, no explanations.
            
            JSON Schema (follow exactly):
            {
              "trip_name": "String (e.g., '5 Days in Paris')",
              "hotels": [
                {
                  "name": "Hotel name",
                  "address": "Full address",
                  "price": "Price range (e.g., '$100-150 per night')",
                  "image_url": "",
                  "geo_coordinates": {
                    "lat": 0.0,
                    "lng": 0.0
                  },
                  "rating": 4.5,
                  "description": "Brief hotel description"
                }
              ],
              "itinerary": [
                {
                  "day": 1,
                  "theme": "Day theme (e.g., 'Historical Landmarks')",
                  "plan": [
                    {
                      "place_name": "Place name",
                      "details": "What to do here",
                      "image_url": "",
                      "geo_coordinates": {
                        "lat": 0.0,
                        "lng": 0.0
                      },
                      "ticket_pricing": "Entry fee or 'Free'",
                      "time_to_travel": "Recommended duration"
                    }
                  ]
                }
              ]
            }
            
            IMPORTANT: Leave image_url as empty string "". Do NOT include any image URLs.
            Include 3-4 hotels and 3-4 activities per day. Use real coordinates for %s.
            """, destination, days, travelers, budget, destination);
    }

    private String extractAndCleanResponse(Map<String, Object> response) {
        try {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            String text = (String) parts.get(0).get("text");

            // Clean the response
            text = text.trim();
            text = text.replaceAll("```json\\s*", "");
            text = text.replaceAll("```\\s*", "");
            text = text.trim();

            // Validate JSON
            JsonParser.parseString(text);

            return text;
        } catch (Exception e) {
            throw new RuntimeException("Error parsing Gemini response: " + e.getMessage());
        }
    }
}