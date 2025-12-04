package com.Shivam.tripplanner.controller;

import com.Shivam.tripplanner.service.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/places")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class PlaceController {

    @Autowired
    private PlaceService placeService;

    @GetMapping("/photo")
    public ResponseEntity<?> getPlacePhoto(@RequestParam String query) {
        try {
            String photoUrl = placeService.getPlacePhoto(query);
            return ResponseEntity.ok(Map.of("url", photoUrl));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}