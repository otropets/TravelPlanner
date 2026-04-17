package org.otropets.travelplanner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.otropets.travelplanner.auth.User;
import org.otropets.travelplanner.auth.UserService;
import org.otropets.travelplanner.budget.Expense;
import org.otropets.travelplanner.budget.ExpenseRepository;
import org.otropets.travelplanner.budget.ExpenseService;
import org.otropets.travelplanner.budget.dto.CreateExpenseDTO;
import org.otropets.travelplanner.budget.dto.ExpenseResponseDTO;
import org.otropets.travelplanner.participant.Participant;
import org.otropets.travelplanner.participant.ParticipantRepository;
import org.otropets.travelplanner.participant.ParticipantStatus;
import org.otropets.travelplanner.participant.TripRole;
import org.otropets.travelplanner.trip.Trip;
import org.otropets.travelplanner.trip.TripRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExpenseServiceTests {

    @InjectMocks
    ExpenseService expenseService;

    @Mock
    TripRepository tripRepository;
    @Mock
    ParticipantRepository participantRepository;
    @Mock
    UserService userService;
    @Mock
    ExpenseRepository expenseRepository;


    @Test
    void createExpenseSuccessfully(){
        User user = User.builder().userId(1L).username("john").password("password123").email("john@gmail.com").firstName("john").lastName("mckena").build();
        Trip trip = Trip.builder().tripId(5L).tripName("Trip to Paris").destination("Paris").startDate(LocalDate.of(2026, 1,1)).endDate(LocalDate.of(2026,1,4)).createdBy(user).build();
        Participant p = Participant.builder().participantId(2L).role(TripRole.ADMIN).trip(trip).user(user).status(ParticipantStatus.ACCEPTED).build();

        when(tripRepository.findById(any())).thenReturn(Optional.ofNullable(trip));
        when(userService.getCurrentUser()).thenReturn(user);
        when(participantRepository.findByTripAndUser(trip, user)).thenReturn(Optional.ofNullable(p));

        CreateExpenseDTO request = new CreateExpenseDTO("coffee", BigDecimal.valueOf(10), " ");
        ExpenseResponseDTO response = expenseService.createExpense(5L, request);

        assertNotNull(response);
        assertEquals("coffee", response.getName());
        assertEquals(BigDecimal.valueOf(10), response.getAmount());
        assertEquals(" ", response.getDescription());
    }

    @Test
    void createExpenseTripNotFound() {
        when(tripRepository.findById(any())).thenReturn(Optional.empty());
        CreateExpenseDTO request = new CreateExpenseDTO("coffee", BigDecimal.valueOf(10), "desc");
        assertThrows(RuntimeException.class, () -> expenseService.createExpense(1L, request));
    }

    @Test
    void createExpenseParticipantNotFound() {
        User user = User.builder().userId(1L).username("john").password("password123").email("john@gmail.com").firstName("john").lastName("mckena").build();
        Trip trip = Trip.builder().tripId(1L).tripName("Trip to Paris").destination("Paris").startDate(LocalDate.of(2026,1,1)).endDate(LocalDate.of(2026,1,4)).createdBy(user).build();

        when(tripRepository.findById(any())).thenReturn(Optional.of(trip));
        when(userService.getCurrentUser()).thenReturn(user);
        when(participantRepository.findByTripAndUser(any(), any())).thenReturn(Optional.empty());

        CreateExpenseDTO request = new CreateExpenseDTO("coffee", BigDecimal.valueOf(10), "desc");
        assertThrows(RuntimeException.class, () -> expenseService.createExpense(1L, request));
    }

    @Test
    void createExpenseGuestNoAccess() {
        User user = User.builder().userId(1L).username("john").password("password123").email("john@gmail.com").firstName("john").lastName("mckena").build();
        Trip trip = Trip.builder().tripId(1L).tripName("Trip to Paris").destination("Paris").startDate(LocalDate.of(2026,1,1)).endDate(LocalDate.of(2026,1,4)).createdBy(user).build();
        Participant p = Participant.builder().participantId(1L).role(TripRole.GUEST).trip(trip).user(user).status(ParticipantStatus.ACCEPTED).build();

        when(tripRepository.findById(any())).thenReturn(Optional.of(trip));
        when(userService.getCurrentUser()).thenReturn(user);
        when(participantRepository.findByTripAndUser(any(), any())).thenReturn(Optional.of(p));

        CreateExpenseDTO request = new CreateExpenseDTO("coffee", BigDecimal.valueOf(10), "desc");
        assertThrows(RuntimeException.class, () -> expenseService.createExpense(1L, request));
    }

    @Test
    void deleteExpenseSuccessfully() {
        User user = User.builder().userId(1L).username("john").password("password123").email("john@gmail.com").firstName("john").lastName("mckena").build();
        Trip trip = Trip.builder().tripId(1L).tripName("Trip to Paris").destination("Paris").startDate(LocalDate.of(2026,1,1)).endDate(LocalDate.of(2026,1,4)).createdBy(user).build();
        Expense expense = Expense.builder().expenseId(1L).name("coffee").amount(BigDecimal.valueOf(10)).trip(trip).createdBy(user).build();

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));
        when(userService.getCurrentUser()).thenReturn(user);

        assertDoesNotThrow(() -> expenseService.deleteExpense(1L));
    }

    @Test
    void deleteExpenseUnauthorized() {
        User user = User.builder().userId(1L).username("john").password("password123").email("john@gmail.com").firstName("john").lastName("mckena").build();
        User otherUser = User.builder().userId(2L).username("mark").password("password123").email("mark@gmail.com").firstName("mark").lastName("smith").build();
        Trip trip = Trip.builder().tripId(1L).tripName("Trip to Paris").destination("Paris").startDate(LocalDate.of(2026,1,1)).endDate(LocalDate.of(2026,1,4)).createdBy(user).build();
        Expense expense = Expense.builder().expenseId(1L).name("coffee").amount(BigDecimal.valueOf(10)).trip(trip).createdBy(user).build();

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));
        when(userService.getCurrentUser()).thenReturn(otherUser);

        assertThrows(RuntimeException.class, () -> expenseService.deleteExpense(1L));
    }

    @Test
    void deleteExpenseNotFound() {
        when(expenseRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> expenseService.deleteExpense(1L));
    }

    @Test
    void getExpensesListSuccessfully() {
        User user = User.builder().userId(1L).username("john").password("password123").email("john@gmail.com").firstName("john").lastName("mckena").build();
        Trip trip = Trip.builder().tripId(1L).tripName("Trip to Paris").destination("Paris").startDate(LocalDate.of(2026,1,1)).endDate(LocalDate.of(2026,1,4)).createdBy(user).build();
        Expense expense1 = Expense.builder().expenseId(1L).name("coffee").amount(BigDecimal.valueOf(10)).trip(trip).createdBy(user).build();
        Expense expense2 = Expense.builder().expenseId(2L).name("dinner").amount(BigDecimal.valueOf(50)).trip(trip).createdBy(user).build();

        when(tripRepository.findById(any())).thenReturn(Optional.of(trip));
        when(expenseRepository.findByTrip(trip)).thenReturn(List.of(expense1, expense2));

        List<ExpenseResponseDTO> response = expenseService.getExpensesList(1L);

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("coffee", response.get(0).getName());
        assertEquals("dinner", response.get(1).getName());
    }


}
