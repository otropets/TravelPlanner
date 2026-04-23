package org.otropets.travelplanner.trip.repository;

import org.otropets.travelplanner.trip.model.Place;
import org.otropets.travelplanner.trip.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceRepository  extends JpaRepository<Place, Long> {
    List<Place> findByTrip(Trip trip);
}
