package org.otropets.travelplanner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.otropets.travelplanner.auth.model.User;
import org.otropets.travelplanner.auth.service.UserService;
import org.otropets.travelplanner.exception.ForbiddenException;
import org.otropets.travelplanner.exception.NotFoundException;
import org.otropets.travelplanner.participant.model.Participant;
import org.otropets.travelplanner.participant.repository.ParticipantRepository;
import org.otropets.travelplanner.participant.model.ParticipantStatus;
import org.otropets.travelplanner.participant.model.TripRole;
import org.otropets.travelplanner.trip.dto.CreatePlaceRequest;
import org.otropets.travelplanner.trip.dto.PlaceResponse;
import org.otropets.travelplanner.trip.model.Place;
import org.otropets.travelplanner.trip.model.Trip;
import org.otropets.travelplanner.trip.repository.PlaceRepository;
import org.otropets.travelplanner.trip.repository.TripRepository;
import org.otropets.travelplanner.trip.service.PlaceService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlaceServiceTests {

    @Mock
    PlaceRepository placeRepository;
    @Mock
    TripRepository tripRepository;
    @Mock
    UserService userService;
    @Mock
    ParticipantRepository participantRepository;

    @InjectMocks
    PlaceService placeService;

    User user = User.builder().userId(1L).username("john").email("john@gmail.com").password("password123").firstName("john").lastName("smith").build();
    Trip trip = Trip.builder().tripId(1L).tripName("Trip to Paris").destination("Paris").startDate(LocalDate.of(2026,1,1)).endDate(LocalDate.of(2026,1,4)).createdBy(user).build();

    @Test
    void getPlaceSuccessfully() {
        Place place = Place.builder().placeId(1L).name("Eiffel Tower").latitude(48.8584).longitude(2.2945).trip(trip).build();
        when(placeRepository.findById(1L)).thenReturn(Optional.of(place));

        PlaceResponse response = placeService.getPlace(1L);

        assertNotNull(response);
        assertEquals("Eiffel Tower", response.getName());
        assertEquals(48.8584, response.getLatitude());
        assertEquals(2.2945, response.getLongitude());
        assertEquals(1L, response.getTripId());
    }

    @Test
    void getPlaceNotFound() {
        when(placeRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> placeService.getPlace(1L));
    }

    @Test
    void addPlaceSuccessfully() {
        Participant participant = Participant.builder().participantId(1L).role(TripRole.ADMIN).trip(trip).user(user).status(ParticipantStatus.ACCEPTED).build();

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(userService.getCurrentUser()).thenReturn(user);
        when(participantRepository.findByTripAndUser(any(), any())).thenReturn(Optional.of(participant));
        when(placeRepository.save(any(Place.class))).thenAnswer(i -> i.getArgument(0));

        CreatePlaceRequest request = new CreatePlaceRequest("Eiffel Tower", 48.8584, 2.2945);
        PlaceResponse response = placeService.addPlace(1L, request);

        assertNotNull(response);
        assertEquals("Eiffel Tower", response.getName());
        assertEquals(48.8584, response.getLatitude());
        assertEquals(2.2945, response.getLongitude());
    }

    @Test
    void addPlaceTripNotFound() {
        when(tripRepository.findById(any())).thenReturn(Optional.empty());
        CreatePlaceRequest request = new CreatePlaceRequest("Eiffel Tower", 48.8584, 2.2945);
        assertThrows(NotFoundException.class, () -> placeService.addPlace(1L, request));
    }

    @Test
    void addPlaceParticipantNotFound() {
        when(tripRepository.findById(any())).thenReturn(Optional.of(trip));
        when(userService.getCurrentUser()).thenReturn(user);
        when(participantRepository.findByTripAndUser(any(), any())).thenReturn(Optional.empty());

        CreatePlaceRequest request = new CreatePlaceRequest("Eiffel Tower", 48.8584, 2.2945);
        assertThrows(NotFoundException.class, () -> placeService.addPlace(1L, request));
    }

    @Test
    void addPlaceGuestForbidden() {
        Participant participant = Participant.builder().participantId(1L).role(TripRole.GUEST).trip(trip).user(user).status(ParticipantStatus.ACCEPTED).build();

        when(tripRepository.findById(any())).thenReturn(Optional.of(trip));
        when(userService.getCurrentUser()).thenReturn(user);
        when(participantRepository.findByTripAndUser(any(), any())).thenReturn(Optional.of(participant));

        CreatePlaceRequest request = new CreatePlaceRequest("Eiffel Tower", 48.8584, 2.2945);
        assertThrows(ForbiddenException.class, () -> placeService.addPlace(1L, request));
    }

    @Test
    void deletePlaceSuccessfully() {
        Place place = Place.builder().placeId(1L).name("Eiffel Tower").latitude(48.8584).longitude(2.2945).trip(trip).build();
        Participant participant = Participant.builder().participantId(2L).role(TripRole.ADMIN).trip(trip).user(user).status(ParticipantStatus.ACCEPTED).build();
        when(placeRepository.findById(1L)).thenReturn(Optional.of(place));
        when(participantRepository.findByTripAndUser(any(), any())).thenReturn(Optional.ofNullable(participant));
        assertDoesNotThrow(() -> placeService.deletePlace(1L));
    }

    @Test
    void deletePlaceNotFound() {
        when(placeRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> placeService.deletePlace(1L));
    }

    @Test
    void getPlacesSuccessfully() {
        Place place1 = Place.builder().placeId(1L).name("Eiffel Tower").latitude(48.8584).longitude(2.2945).trip(trip).build();
        Place place2 = Place.builder().placeId(2L).name("Louvre").latitude(48.8606).longitude(2.3376).trip(trip).build();

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(placeRepository.findByTrip(trip)).thenReturn(List.of(place1, place2));

        List<PlaceResponse> response = placeService.getPlaces(1L);

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("Eiffel Tower", response.get(0).getName());
        assertEquals("Louvre", response.get(1).getName());
    }

    @Test
    void getPlacesTripNotFound() {
        when(tripRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> placeService.getPlaces(1L));
    }
}