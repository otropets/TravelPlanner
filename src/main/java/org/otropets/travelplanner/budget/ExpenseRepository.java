package org.otropets.travelplanner.budget;

import org.otropets.travelplanner.trip.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByTrip(Trip trip);
}

