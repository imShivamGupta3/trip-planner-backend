package com.Shivam.tripplanner.service;

import org.springframework.stereotype.Service;

@Service
public class ImageService {

    public String getPlaceImage(String placeName, String destination) {
        // Use Unsplash Source API - no API key needed for basic usage
        String query = placeName + " " + destination;
        String encodedQuery = query.replace(" ", "+");
        return String.format("https://source.unsplash.com/800x600/?%s", encodedQuery);
    }

    public String getHotelImage(String hotelName, String destination) {
        String query = hotelName + " hotel " + destination;
        String encodedQuery = query.replace(" ", "+");
        return String.format("https://source.unsplash.com/800x600/?%s", encodedQuery);
    }

    public String getDestinationImage(String destination) {
        String encodedQuery = destination.replace(" ", "+");
        return String.format("https://source.unsplash.com/1600x900/?%s,travel,city", encodedQuery);
    }
}