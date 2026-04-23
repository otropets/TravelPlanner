package org.otropets.travelplanner.budget.repository;

import org.otropets.travelplanner.budget.model.Expense;
import org.otropets.travelplanner.trip.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByTrip(Trip trip);
}

