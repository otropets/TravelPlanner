package org.otropets.travelplanner.participant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.otropets.travelplanner.participant.model.ParticipantStatus;
import org.otropets.travelplanner.participant.model.TripRole;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantResponse {

    private Long participantId;
    private String username;
    private String email;
    private TripRole role;
    private ParticipantStatus status;
    private LocalDateTime joinedAt;
}
