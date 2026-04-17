package org.otropets.travelplanner.budget;

import org.otropets.travelplanner.auth.User;
import org.otropets.travelplanner.auth.UserService;
import org.otropets.travelplanner.budget.dto.CreateExpenseDTO;
import org.otropets.travelplanner.budget.dto.ExpenseResponseDTO;
import org.otropets.travelplanner.participant.Participant;
import org.otropets.travelplanner.participant.ParticipantRepository;
import org.otropets.travelplanner.participant.TripRole;
import org.otropets.travelplanner.trip.Trip;
import org.otropets.travelplanner.trip.TripRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserService userService;
    private final TripRepository tripRepository;
    private final ParticipantRepository participantRepository;
    public ExpenseService(ExpenseRepository expenseRepository, UserService userService, TripRepository tripRepository, ParticipantRepository participantRepository)
    {
        this.expenseRepository = expenseRepository;
        this.userService = userService;
        this.tripRepository = tripRepository;
        this.participantRepository = participantRepository;
    }


    public ExpenseResponseDTO createExpense(Long tripId, CreateExpenseDTO request) {
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new RuntimeException("trip not found"));
        User user = userService.getCurrentUser();
        Participant participant = participantRepository.findByTripAndUser(trip, user).orElseThrow(() -> new RuntimeException("participant not found"));

        if (participant.getRole() != TripRole.ADMIN && participant.getRole() != TripRole.PARTICIPANT) {
            throw new RuntimeException("no access to create expenses");
        }
        Expense expense = Expense.builder().name(request.getName()).amount(request.getAmount()).createdBy(user).trip(trip).description(request.getDescription()).build();
        expenseRepository.save(expense);
        return new ExpenseResponseDTO(expense.getExpenseId(), expense.getName(), expense.getAmount(), expense.getTrip().getTripId(), expense.getCreatedAt(), expense.getCreatedBy().getUsername(), expense.getDescription());
    }

    public void deleteExpense(Long expenseId){
        Expense expense = expenseRepository.findById(expenseId).orElseThrow(() -> new RuntimeException("expense not found"));
        if(!expense.getCreatedBy().getUserId().equals(userService.getCurrentUser().getUserId())){
            throw new RuntimeException("no access, you have not created this expense");
        }
        expenseRepository.delete(expense);
    }

    public ExpenseResponseDTO getExpense(Long expenseId){
        Expense expense = expenseRepository.findById(expenseId).orElseThrow(() -> new RuntimeException("expense not found"));
        return new ExpenseResponseDTO(expense.getExpenseId(), expense.getName(), expense.getAmount(), expense.getTrip().getTripId(), expense.getCreatedAt(), expense.getCreatedBy().getUsername(), expense.getDescription());
    }


    public List<ExpenseResponseDTO> getExpensesList(Long tripId){
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new RuntimeException("trip not found"));

        List <Expense> expenses = expenseRepository.findByTrip(trip);
        List <ExpenseResponseDTO> res = new ArrayList<>();

        for(Expense e : expenses){
            ExpenseResponseDTO response = new ExpenseResponseDTO(e.getExpenseId(), e.getName(), e.getAmount(), e.getTrip().getTripId(), e.getCreatedAt(), e.getCreatedBy().getUsername(), e.getDescription());
            res.add(response);
        }
        return res;
    }

}
