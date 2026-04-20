package org.otropets.travelplanner.trip.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceResponse {
    private Long placeId;
    private String name;
    private Double latitude;
    private Double longitude;
    private Long tripId;
}