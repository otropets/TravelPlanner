package org.otropets.travelplanner.participant.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.otropets.travelplanner.participant.TripRole;

@Data
public class InviteRequest {

    @NotBlank
    private String email;
    private TripRole tripRole;

}
