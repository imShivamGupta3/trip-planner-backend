package com.Shivam.tripplanner.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.Shivam.tripplanner.dto.TripRequest;
import com.Shivam.tripplanner.entity.Trip;
import com.Shivam.tripplanner.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TripService {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private ImageService imageService;

    private final Gson gson = new Gson();

    public Trip generateTrip(TripRequest request) {
        // Generate AI response
        String tripData = geminiService.generateTripPlan(
                request.getDestination(),
                request.getDays(),
                request.getBudget(),
                request.getTravelers()
        );

        // Add images to the trip data
        tripData = addImagesToTripData(tripData, request.getDestination());

        // Calculate dates
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(request.getDays());

        // Create trip
        Trip trip = Trip.builder()
                .userId(request.getUserId())
                .destination(request.getDestination())
                .days(request.getDays())
                .startDate(startDate)
                .endDate(endDate)
                .budget(request.getBudget())
                .travelers(request.getTravelers())
                .tripData(tripData)
                .build();

        return tripRepository.save(trip);
    }

    private String addImagesToTripData(String tripDataJson, String destination) {
        try {
            JsonObject tripData = JsonParser.parseString(tripDataJson).getAsJsonObject();

            // Add images to hotels
            if (tripData.has("hotels")) {
                JsonArray hotels = tripData.getAsJsonArray("hotels");
                for (int i = 0; i < hotels.size(); i++) {
                    JsonObject hotel = hotels.get(i).getAsJsonObject();
                    String hotelName = hotel.get("name").getAsString();
                    String imageUrl = imageService.getHotelImage(hotelName, destination);
                    hotel.addProperty("image_url", imageUrl);
                }
            }

            // Add images to itinerary places
            if (tripData.has("itinerary")) {
                JsonArray itinerary = tripData.getAsJsonArray("itinerary");
                for (int i = 0; i < itinerary.size(); i++) {
                    JsonObject day = itinerary.get(i).getAsJsonObject();
                    if (day.has("plan")) {
                        JsonArray places = day.getAsJsonArray("plan");
                        for (int j = 0; j < places.size(); j++) {
                            JsonObject place = places.get(j).getAsJsonObject();
                            String placeName = place.get("place_name").getAsString();
                            String imageUrl = imageService.getPlaceImage(placeName, destination);
                            place.addProperty("image_url", imageUrl);
                        }
                    }
                }
            }

            return gson.toJson(tripData);
        } catch (Exception e) {
            // If image addition fails, return original data
            return tripDataJson;
        }
    }

    public List<Trip> getUserTrips(Long userId) {
        return tripRepository.findByUserId(userId);
    }

    public Trip getTripById(Long tripId) {
        return tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));
    }
}