package org.otropets.travelplanner.participant;

import org.otropets.travelplanner.trip.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    List<Participant> findByTrip(Trip trip);
}

