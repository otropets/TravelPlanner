package org.otropets.travelplanner.trip;

import org.otropets.travelplanner.auth.UserRepository;
import org.otropets.travelplanner.auth.UserService;
import org.otropets.travelplanner.trip.dto.CreateTripRequest;
import org.otropets.travelplanner.trip.dto.TripResponse;
import org.springframework.stereotype.Service;


@Service
public class TripService {
    private final UserService userService;
    private final TripRepository tripRepository;
    public TripService(UserService userService, TripRepository tripRepository) {
        this.userService = userService;
        this.tripRepository = tripRepository;
    }

    public TripResponse createTrip(CreateTripRequest request)
    {
        Trip trip = Trip.builder()
                .tripName(request.getTripName())
                .destination(request.getDestination())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .createdBy(userService.getCurrentUser())
                .build();
        tripRepository.save(trip);
        return new TripResponse(trip.getTripId(), trip.getTripName(), trip.getDestination(), trip.getStartDate(), trip.getEndDate(), trip.getCreatedAt(), trip.getCreatedBy().getUsername());
    }

    public void deleteTrip(Long tripId){

        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new RuntimeException("Trip not found"));
        if(!trip.getCreatedBy().getUserId().equals(userService.getCurrentUser().getUserId())){
            throw new RuntimeException("No access");

        }
        tripRepository.delete(trip);
    }
    


}
