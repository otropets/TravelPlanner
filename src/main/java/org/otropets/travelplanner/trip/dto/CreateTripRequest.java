package org.otropets.travelplanner.trip.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTripRequest {
    @NotBlank
    private String tripName;
    @NotBlank
    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;
}
