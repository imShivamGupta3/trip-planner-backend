package com.Shivam.tripplanner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripRequest {
    private String destination;
    private Integer days;
    private String budget;
    private String travelers;
    private Long userId;
}