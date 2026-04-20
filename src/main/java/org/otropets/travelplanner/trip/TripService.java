package org.otropets.travelplanner.trip;

import org.otropets.travelplanner.auth.User;
import org.otropets.travelplanner.auth.UserService;
import org.otropets.travelplanner.exception.ForbiddenException;
import org.otropets.travelplanner.exception.NotFoundException;
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
import java.util.stream.Collectors;


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

        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new NotFoundException("Trip not found"));
        Participant p = participantRepository.findByTripAndUser(trip, userService.getCurrentUser()).orElseThrow(() -> new NotFoundException("Participant not found"));
        if(p.getRole() != TripRole.ADMIN || p.getStatus() != ParticipantStatus.ACCEPTED)
        {
            throw new ForbiddenException("No access");
        }
        List<Participant> participants = participantRepository.findByTrip(trip);
        participantRepository.deleteAll(participants);

        tripRepository.delete(trip);
    }
    public TripResponse getTrip(Long tripId){
        Trip trip = tripRepository.findById(tripId).orElseThrow( () -> new NotFoundException("Trip not found"));
        return new TripResponse(trip.getTripId(), trip.getTripName(),trip.getDestination(), trip.getStartDate(), trip.getEndDate(), trip.getCreatedAt(), trip.getCreatedBy().getUsername());
    }

    public TripResponse updateTrip(Long tripId, UpdateTripRequest updateTripRequest){
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new NotFoundException("Trip not found"));
        Participant p = participantRepository.findByTripAndUser(trip, userService.getCurrentUser()).orElseThrow(() -> new NotFoundException("Participant not found"));
        if(p.getRole() != TripRole.ADMIN || p.getStatus() != ParticipantStatus.ACCEPTED){
            throw new ForbiddenException("No access");
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

    public List<TripResponse> getUserTrips() {
        User currentUser = userService.getCurrentUser();

        List<Participant> participations = participantRepository
                .findByUserAndStatus(currentUser, ParticipantStatus.ACCEPTED);

        return participations.stream()
                .map(p -> new TripResponse(
                        p.getTrip().getTripId(),
                        p.getTrip().getTripName(),
                        p.getTrip().getDestination(),
                        p.getTrip().getStartDate(),
                        p.getTrip().getEndDate(),
                        p.getTrip().getCreatedAt(),
                        p.getTrip().getCreatedBy().getUsername()
                ))
                .collect(Collectors.toList());
    }


}
