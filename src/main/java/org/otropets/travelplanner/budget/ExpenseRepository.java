package org.otropets.travelplanner.budget;

import org.otropets.travelplanner.trip.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    Optional<Expense> findById();
    List<Expense> findByTrip(Trip trip);
}

