package org.otropets.travelplanner.participant.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.otropets.travelplanner.participant.model.TripRole;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InviteRequest {

    @NotBlank
    private String email;
    private TripRole tripRole;

}
