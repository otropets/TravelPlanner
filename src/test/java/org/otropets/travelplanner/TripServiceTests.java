package org.otropets.travelplanner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.otropets.travelplanner.auth.model.User;
import org.otropets.travelplanner.auth.service.UserService;
import org.otropets.travelplanner.participant.model.Participant;
import org.otropets.travelplanner.participant.repository.ParticipantRepository;
import org.otropets.travelplanner.participant.model.ParticipantStatus;
import org.otropets.travelplanner.participant.model.TripRole;
import org.otropets.travelplanner.trip.repository.PlaceRepository;
import org.otropets.travelplanner.trip.model.Trip;
import org.otropets.travelplanner.trip.repository.TripRepository;
import org.otropets.travelplanner.trip.service.TripService;
import org.otropets.travelplanner.trip.dto.CreateTripRequest;
import org.otropets.travelplanner.trip.dto.TripResponse;
import org.otropets.travelplanner.trip.dto.UpdateTripRequest;

import java.time.LocalDate;
import java.util.List;
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

    @Mock
    PlaceRepository placeRepository;

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
        when(repository.findById(2L)).thenReturn(Optional.of(mockTrip));
        when(userService.getCurrentUser()).thenReturn(mockUser2);
        when(participantRepository.findByTripAndUser(any(), any())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> tripService.deleteTrip(2L));
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

    @Test
    void getUserTripsSuccessfully() {
        User mockUser = User.builder().userId(1L).username("john").password("password123")
                .email("john@gmail.com").firstName("john").lastName("mcneil").build();
        Trip trip1 = Trip.builder().tripId(1L).tripName("Trip to Paris").destination("Paris")
                .startDate(LocalDate.of(2026,1,1)).endDate(LocalDate.of(2026,1,4)).createdBy(mockUser).build();
        Trip trip2 = Trip.builder().tripId(2L).tripName("Trip to Rome").destination("Rome")
                .startDate(LocalDate.of(2026,2,1)).endDate(LocalDate.of(2026,2,4)).createdBy(mockUser).build();
        Participant p1 = Participant.builder().participantId(1L).role(TripRole.ADMIN).trip(trip1).user(mockUser).status(ParticipantStatus.ACCEPTED).build();
        Participant p2 = Participant.builder().participantId(2L).role(TripRole.PARTICIPANT).trip(trip2).user(mockUser).status(ParticipantStatus.ACCEPTED).build();

        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(participantRepository.findByUserAndStatus(mockUser, ParticipantStatus.ACCEPTED))
                .thenReturn(List.of(p1, p2));

        List<TripResponse> response = tripService.getUserTrips();

        assertNotNull(response);
        assertEquals(2, response.size());
    }

}
