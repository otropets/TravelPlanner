package org.otropets.travelplanner.trip;


import org.otropets.travelplanner.exception.NotFoundException;
import org.otropets.travelplanner.trip.dto.CreatePlaceRequest;
import org.otropets.travelplanner.trip.dto.PlaceResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlaceService {
    private final PlaceRepository placeRepository;
    private final TripRepository tripRepository;

    public PlaceService(PlaceRepository placeRepository, TripRepository tripRepository)
    {
        this.placeRepository = placeRepository;
        this.tripRepository = tripRepository;
    }

    public PlaceResponse getPlace(Long placeId) {
        Place p = placeRepository.findById(placeId).orElseThrow(() -> new NotFoundException("Place not found"));
        return new PlaceResponse(p.getPlaceId(), p.getName(), p.getLatitude(), p.getLongitude(), p.getTrip().getTripId());
    }

    public PlaceResponse addPlace(Long tripId, CreatePlaceRequest request) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NotFoundException("Trip not found"));
        Place place = Place.builder().name(request.getName()).latitude(request.getLatitude()).longitude(request.getLongitude()).trip(trip).build();
        placeRepository.save(place);
        return new PlaceResponse(place.getPlaceId(), place.getName(), place.getLatitude(), place.getLongitude(), tripId);
    }

    public void deletePlace(Long placeId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new NotFoundException("Place not found"));
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
