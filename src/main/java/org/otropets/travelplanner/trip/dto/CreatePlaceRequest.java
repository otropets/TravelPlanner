package org.otropets.travelplanner.trip.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePlaceRequest {
    @NotBlank
    private String name;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

}
