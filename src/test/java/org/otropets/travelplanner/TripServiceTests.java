package org.otropets.travelplanner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.otropets.travelplanner.auth.User;
import org.otropets.travelplanner.auth.UserService;
import org.otropets.travelplanner.trip.Trip;
import org.otropets.travelplanner.trip.TripRepository;
import org.otropets.travelplanner.trip.TripService;
import org.otropets.travelplanner.trip.dto.CreateTripRequest;
import org.otropets.travelplanner.trip.dto.TripResponse;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class TripServiceTests {
    @InjectMocks
    TripService tripService;

    @Mock
    TripRepository repository;

    @Mock
    UserService userService;

    @Test
    void createTripSuccessfully(){
        User mockUser = User.builder()
                .userId(1L)
                .username("john")
                .email("john@example.com")
                .password("hashed")
                .firstName("John")
                .lastName("Doe")
                .build();

        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(repository.save(any(Trip.class))).thenAnswer(i -> i.getArgument(0));
        CreateTripRequest request = new CreateTripRequest("Trip to Denmark","Copenhagen", LocalDate.of(2026, 2, 3), LocalDate.of(2026, 2,6));
        TripResponse response = tripService.createTrip(request);
        assertNotNull(response);
        assertEquals("Trip to Denmark", response.getTripName());
        assertEquals("Copenhagen", response.getDestination());
    }

    @Test
    void deleteTripSuccessfully(){
        User mockUser = User.builder().userId(1L).username("john").password("password123").email("john@email.com").firstName("john").lastName("mcneil").build();
        Trip mockTrip = Trip.builder().tripName("trip to Paris").destination("Paris").createdBy(mockUser).startDate(LocalDate.of(2026, 1,1)).endDate(LocalDate.of(2026,1,4)).build();
        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(repository.findById(1L)).thenReturn(Optional.of(mockTrip));
        assertDoesNotThrow(()-> tripService.deleteTrip(1L));
    }




}
