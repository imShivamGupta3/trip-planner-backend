package com.Shivam.tripplanner.controller;

import com.Shivam.tripplanner.config.RateLimitConfig;
import com.Shivam.tripplanner.dto.TripRequest;
import com.Shivam.tripplanner.entity.Trip;
import com.Shivam.tripplanner.entity.User; // Import User Entity
import com.Shivam.tripplanner.repository.UserRepository; // Import Repo
import com.Shivam.tripplanner.service.TripService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal; // Import Principal
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/trips")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class TripController {

    @Autowired
    private TripService tripService;

    @Autowired
    private RateLimitConfig rateLimitConfig;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/generate")
    public ResponseEntity<?> generateTrip(@RequestBody TripRequest request,
                                          HttpServletRequest httpRequest,
                                          Principal principal) {

        // Rate Limiting Logic
        String clientIp = httpRequest.getRemoteAddr();
        Bucket bucket = rateLimitConfig.resolveBucket(clientIp);
        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("error", "Rate limit exceeded. Maximum 5 requests per hour."));
        }

        try {
            //Get the User ID from the Token, not the Request Body
            String userEmail = principal.getName();
            Optional<User> user = userRepository.findByEmail(userEmail);

            if (user.isPresent()) {
                request.setUserId(user.get().getId());
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not found"));
            }

            Trip trip = tripService.generateTrip(request);
            return ResponseEntity.ok(trip);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Trip>> getUserTrips(@PathVariable Long userId) {
        List<Trip> trips = tripService.getUserTrips(userId);
        return ResponseEntity.ok(trips);
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<?> getTripById(@PathVariable Long tripId) {
        try {
            Trip trip = tripService.getTripById(tripId);
            return ResponseEntity.ok(trip);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}