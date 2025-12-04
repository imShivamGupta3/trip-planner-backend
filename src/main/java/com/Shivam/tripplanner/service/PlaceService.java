package com.Shivam.tripplanner.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

@Service
public class PlaceService {

    @Value("${google.places.api.key}")
    private String apiKey;

    private final String TEXT_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json?query={query}&key={key}";
    private final String PHOTO_URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference={ref}&key={key}";

    public String getPlacePhoto(String placeName) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            String searchUrl = TEXT_SEARCH_URL.replace("{query}", placeName).replace("{key}", apiKey);
            ResponseEntity<String> response = restTemplate.getForEntity(searchUrl, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            if (root.has("results") && root.get("results").isArray() && root.get("results").size() > 0) {
                JsonNode place = root.get("results").get(0);
                if (place.has("photos") && place.get("photos").isArray()) {
                    String photoRef = place.get("photos").get(0).get("photo_reference").asText();
                    return PHOTO_URL.replace("{ref}", photoRef).replace("{key}", apiKey);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Fallback Image
        return "https://images.unsplash.com/photo-1476514525535-07fb3b4ae5f1?q=80&w=2070&auto=format&fit=crop";
    }
}