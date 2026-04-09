package org.otropets.travelplanner.trip.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTripRequest {
    private String tripName;
    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;
}
