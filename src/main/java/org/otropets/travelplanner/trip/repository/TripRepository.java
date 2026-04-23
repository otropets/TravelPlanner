package org.otropets.travelplanner.trip.repository;

import org.otropets.travelplanner.auth.model.User;
import org.otropets.travelplanner.trip.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByCreatedBy(User createdBy);
}
