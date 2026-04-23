package org.otropets.travelplanner.participant.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.otropets.travelplanner.auth.model.User;
import org.otropets.travelplanner.trip.model.Trip;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "participants")
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long participantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TripRole role;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime joinedAt;

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private ParticipantStatus status;

}
