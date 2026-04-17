package org.otropets.travelplanner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.otropets.travelplanner.auth.User;
import org.otropets.travelplanner.auth.UserService;
import org.otropets.travelplanner.participant.Participant;
import org.otropets.travelplanner.participant.ParticipantRepository;
import org.otropets.travelplanner.participant.ParticipantStatus;
import org.otropets.travelplanner.participant.TripRole;
import org.otropets.travelplanner.trip.Trip;
import org.otropets.travelplanner.trip.TripRepository;
import org.otropets.travelplanner.trip.TripService;
import org.otropets.travelplanner.trip.dto.CreateTripRequest;
import org.otropets.travelplanner.trip.dto.TripResponse;
import org.otropets.travelplanner.trip.dto.UpdateTripRequest;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class TripServiceTests {
    @InjectMocks
    TripService tripService;

    @Mock
    TripRepository repository;

    @Mock
    ParticipantRepository participantRepository;
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
        Participant p = Participant.builder().participantId(2L).role(TripRole.ADMIN).trip(mockTrip).user(mockUser).status(ParticipantStatus.ACCEPTED).build();
        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(repository.findById(1L)).thenReturn(Optional.of(mockTrip));
        when(participantRepository.findByTripAndUser(any(), any())).thenReturn(Optional.ofNullable(p));
        assertDoesNotThrow(()-> tripService.deleteTrip(1L));
    }

    @Test
    void getTripSuccessfully(){
        User mockuser = User.builder().userId(1L).username("john")
                .password("password123").email("john@gmail.com").firstName("john")
                .lastName("mcneil").build();
        Trip mockTrip = Trip.builder().tripId(2L).tripName("Trip to Paris").destination("Paris")
                .startDate(LocalDate.of(2026, 1,1)).endDate(LocalDate.of(2026, 1, 4)).createdBy(mockuser).build();
        when(repository.findById(2L)).thenReturn(Optional.of(mockTrip));

        TripResponse response = tripService.getTrip(2L);

        assertEquals("Trip to Paris", response.getTripName());
        assertEquals("Paris", response.getDestination());
        assertEquals(LocalDate.of(2026, 1, 1), response.getStartDate());
        assertEquals(LocalDate.of(2026, 1, 4), response.getEndDate());
    }

    @Test
    void deleteTripUnsuccessfullyTripNotFound(){
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> tripService.deleteTrip(1L));
    }
    @Test
    void deleteTripUnsuccessfullyNoAccess() {
        User mockUser = User.builder().userId(1L).username("john").password("password123").email("john@email.com").firstName("john").lastName("mcneil").build();
        User mockUser2 = User.builder().userId(2L).username("john").password("password123").email("john@email.com").firstName("john").lastName("mcneil").build();

        Trip mockTrip = Trip.builder().tripId(2L).tripName("Trip to Paris").destination("Paris")
                .startDate(LocalDate.of(2026, 1,1)).endDate(LocalDate.of(2026, 1, 4)).createdBy(mockUser).build();
        when(repository.findById(1L)).thenReturn(Optional.of(mockTrip));
        when(userService.getCurrentUser()).thenReturn(mockUser2);

        assertThrows(RuntimeException.class, () -> tripService.deleteTrip(1L));
    }

    @Test
    void updateTripSuccessfully(){
        User mockUser = User.builder().userId(1L).username("john").password("password123").email("john@email.com").firstName("john").lastName("mcneil").build();
        Trip mockTrip = Trip.builder().tripId(2L).tripName("Trip to Paris").destination("Paris")
                .startDate(LocalDate.of(2026, 1,1)).endDate(LocalDate.of(2026, 1, 4)).createdBy(mockUser).build();
        Participant p = Participant.builder().participantId(3L).role(TripRole.ADMIN).trip(mockTrip).user(mockUser).status(ParticipantStatus.ACCEPTED).build();
        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(repository.findById(2L)).thenReturn(Optional.of(mockTrip));
        when(participantRepository.findByTripAndUser(any(), any())).thenReturn(Optional.of(p));
        UpdateTripRequest request = new UpdateTripRequest("updated trip to Paris", null, null, null);
        TripResponse response = tripService.updateTrip(2L, request);

        assertNotNull(response);
        assertEquals("updated trip to Paris", response.getTripName());
        assertEquals("Paris", response.getDestination());
    }


}
