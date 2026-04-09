package org.otropets.travelplanner.trip;

import org.otropets.travelplanner.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByCreatedBy(User createdBy);
}
