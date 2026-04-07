package org.otropets.travelplanner.trip.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripResponse {
    private Long tripId;
    private String tripName;
    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;
    private String createdByUsername;
}
