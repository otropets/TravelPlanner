package org.otropets.travelplanner.participant.repository;

import org.otropets.travelplanner.auth.model.User;
import org.otropets.travelplanner.participant.model.Participant;
import org.otropets.travelplanner.participant.model.ParticipantStatus;
import org.otropets.travelplanner.trip.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    List<Participant> findByTrip(Trip trip);
    Optional<Participant> findByTripAndUser(Trip trip, User user);
    List<Participant> findByUserAndStatus(User user, ParticipantStatus status);
}

