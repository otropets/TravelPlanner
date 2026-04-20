package org.otropets.travelplanner.trip.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePlaceRequest {
    @NotBlank
    private String name;

    @NotBlank
    private Double latitude;

    @NotBlank
    private Double longitude;

}
