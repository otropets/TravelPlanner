package org.otropets.travelplanner.trip;

import org.otropets.travelplanner.auth.User;
import org.otropets.travelplanner.auth.UserService;
import org.otropets.travelplanner.participant.Participant;
import org.otropets.travelplanner.participant.ParticipantRepository;
import org.otropets.travelplanner.participant.ParticipantStatus;
import org.otropets.travelplanner.participant.TripRole;
import org.otropets.travelplanner.trip.dto.CreateTripRequest;
import org.otropets.travelplanner.trip.dto.TripResponse;
import org.otropets.travelplanner.trip.dto.UpdateTripRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;


@Service
public class TripService {
    private final UserService userService;
    private final TripRepository tripRepository;
    private final ParticipantRepository participantRepository;
    public TripService(UserService userService, TripRepository tripRepository, ParticipantRepository participantRepository) {
        this.userService = userService;
        this.tripRepository = tripRepository;
        this.participantRepository = participantRepository;
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

        Participant participant = Participant.builder().role(TripRole.ADMIN).trip(trip).user(userService.getCurrentUser()).status(ParticipantStatus.ACCEPTED).build();
        participantRepository.save(participant);

        return new TripResponse(trip.getTripId(), trip.getTripName(), trip.getDestination(), trip.getStartDate(), trip.getEndDate(), trip.getCreatedAt(), trip.getCreatedBy().getUsername());
    }

    public void deleteTrip(Long tripId){

        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new RuntimeException("Trip not found"));
        // add a check for budget beeing handled later
        Participant p = participantRepository.findByTripAndUser(trip, userService.getCurrentUser()).orElseThrow(() -> new RuntimeException("partiipant not found"));
        if(p.getRole() != TripRole.ADMIN)
        {
            throw new RuntimeException("No access");
        }
        tripRepository.delete(trip);
    }
    public TripResponse getTrip(Long tripId){
        Trip trip = tripRepository.findById(tripId).orElseThrow( () -> new RuntimeException("trip not found"));
        return new TripResponse(trip.getTripId(), trip.getTripName(),trip.getDestination(), trip.getStartDate(), trip.getEndDate(), trip.getCreatedAt(), trip.getCreatedBy().getUsername());
    }

    public TripResponse updateTrip(Long tripId, UpdateTripRequest updateTripRequest){
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new RuntimeException("Trip not found"));
        Participant p = participantRepository.findByTripAndUser(trip, userService.getCurrentUser()).orElseThrow(() -> new RuntimeException("participant not found"));
        if(p.getRole() != TripRole.ADMIN){
            throw new RuntimeException("No access");
        }
        if(updateTripRequest.getTripName() != null){
            trip.setTripName(updateTripRequest.getTripName());
        }
        if(updateTripRequest.getDestination() != null){
            trip.setDestination(updateTripRequest.getDestination());
        }
        if(updateTripRequest.getStartDate() != null)
        {
            trip.setStartDate(updateTripRequest.getStartDate());
        }
        if(updateTripRequest.getEndDate() != null){
            trip.setEndDate(updateTripRequest.getEndDate());
        }
        tripRepository.save(trip);
        return new TripResponse(trip.getTripId(), trip.getTripName(), trip.getDestination(), trip.getStartDate(), trip.getEndDate(), trip.getCreatedAt(), trip.getCreatedBy().getUsername());
    }

    public List<TripResponse> getUserTrips(){
        User cur_user = userService.getCurrentUser();
        List <Trip> trips = tripRepository.findByCreatedBy(cur_user);
        List <TripResponse> result = new ArrayList<>();
        for(Trip trip : trips){
            TripResponse response = new TripResponse(trip.getTripId(),trip.getTripName(), trip.getDestination(), trip.getStartDate(), trip.getEndDate(), trip.getCreatedAt(), trip.getCreatedBy().getUsername());
            result.add(response);
        }
        return result;
    }


}
