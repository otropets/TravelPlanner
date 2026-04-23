package org.otropets.travelplanner.trip.service;


import org.otropets.travelplanner.auth.model.User;
import org.otropets.travelplanner.auth.service.UserService;
import org.otropets.travelplanner.exception.ForbiddenException;
import org.otropets.travelplanner.exception.NotFoundException;
import org.otropets.travelplanner.participant.model.Participant;
import org.otropets.travelplanner.participant.repository.ParticipantRepository;
import org.otropets.travelplanner.participant.model.TripRole;
import org.otropets.travelplanner.trip.repository.PlaceRepository;
import org.otropets.travelplanner.trip.repository.TripRepository;
import org.otropets.travelplanner.trip.dto.CreatePlaceRequest;
import org.otropets.travelplanner.trip.dto.PlaceResponse;
import org.otropets.travelplanner.trip.model.Place;
import org.otropets.travelplanner.trip.model.Trip;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlaceService {
    private final PlaceRepository placeRepository;
    private final TripRepository tripRepository;
    private final UserService userService;
    private final ParticipantRepository participantRepository;

    public PlaceService(PlaceRepository placeRepository, TripRepository tripRepository, UserService userService, ParticipantRepository participantRepository)
    {
        this.placeRepository = placeRepository;
        this.tripRepository = tripRepository;
        this.userService = userService;
        this.participantRepository = participantRepository;
    }

    public PlaceResponse getPlace(Long placeId) {
        Place p = placeRepository.findById(placeId).orElseThrow(() -> new NotFoundException("Place not found"));
        return new PlaceResponse(p.getPlaceId(), p.getName(), p.getLatitude(), p.getLongitude(), p.getTrip().getTripId());
    }

    public PlaceResponse addPlace(Long tripId, CreatePlaceRequest request) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NotFoundException("Trip not found"));
        User user = userService.getCurrentUser();
        Participant p = participantRepository.findByTripAndUser(trip, user).orElseThrow(() -> new NotFoundException("Participant not found"));
        if(p.getRole() == TripRole.GUEST)
        {
            throw new ForbiddenException("Guests can not add new places");
        }

        Place place = Place.builder().name(request.getName()).latitude(request.getLatitude()).longitude(request.getLongitude()).trip(trip).build();
        placeRepository.save(place);
        return new PlaceResponse(place.getPlaceId(), place.getName(), place.getLatitude(), place.getLongitude(), tripId);
    }

    public void deletePlace(Long placeId) {

        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new NotFoundException("Place not found"));

        Trip trip = place.getTrip();
        User user = userService.getCurrentUser();
        Participant p = participantRepository.findByTripAndUser(trip,user).orElseThrow(() -> new NotFoundException("Participant not found"));
        if(p.getRole() == TripRole.GUEST){
            throw new ForbiddenException("Guests are not allowed to delete places");
        }
        placeRepository.delete(place);
    }

    public List<PlaceResponse> getPlaces(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NotFoundException("Trip not found"));
        return placeRepository.findByTrip(trip).stream()
                .map(p -> new PlaceResponse(p.getPlaceId(), p.getName(), p.getLatitude(), p.getLongitude(), tripId))
                .collect(Collectors.toList());
    }

}
