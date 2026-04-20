package org.otropets.travelplanner.participant;

import org.otropets.travelplanner.auth.User;
import org.otropets.travelplanner.auth.UserRepository;
import org.otropets.travelplanner.auth.UserService;
import org.otropets.travelplanner.exception.BadRequestException;
import org.otropets.travelplanner.exception.ForbiddenException;
import org.otropets.travelplanner.exception.NotFoundException;
import org.otropets.travelplanner.participant.dto.InviteRequest;
import org.otropets.travelplanner.participant.dto.ParticipantResponse;
import org.otropets.travelplanner.trip.Trip;
import org.otropets.travelplanner.trip.TripRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final TripRepository tripRepository;
    private final UserService userService;

    public ParticipantService(ParticipantRepository participantRepository, UserRepository userRepository, TripRepository tripRepository, UserService userService)
    {
        this.participantRepository = participantRepository;
        this.userRepository = userRepository;
        this.tripRepository = tripRepository;
        this.userService = userService;
    }

    public ParticipantResponse inviteParticipant(Long tripId, InviteRequest request){
        User cur_user = userService.getCurrentUser();
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new NotFoundException("Trip not found"));
        Participant p = participantRepository.findByTripAndUser(trip, cur_user).orElseThrow(()->new NotFoundException("Participant not found"));

        if(p.getRole() != TripRole.ADMIN)
        {
            throw new ForbiddenException("No permission to invite new participant");
        }

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new NotFoundException("User not found"));
        Participant participant = Participant.builder().role(request.getTripRole()).trip(trip).user(user).status(ParticipantStatus.PENDING).build();
        participantRepository.save(participant);

        return new ParticipantResponse(participant.getParticipantId(), user.getUsername(), user.getEmail(), participant.getRole(), participant.getStatus(), participant.getJoinedAt());

    }

    public ParticipantResponse getParticipant(Long participantId){
        Participant participant = participantRepository.findById(participantId).orElseThrow(()->new NotFoundException("Participant not found"));
        return new ParticipantResponse(participant.getParticipantId(), participant.getUser().getUsername(), participant.getUser().getEmail(), participant.getRole(), participant.getStatus(),participant.getJoinedAt());
    }

    public List<ParticipantResponse> getParticipantsList(Long tripId){
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new NotFoundException("Trip not found"));
        List <Participant> participants = participantRepository.findByTrip(trip);
        List <ParticipantResponse> result = new ArrayList<>();

        for(Participant p : participants)
        {
           ParticipantResponse response = new ParticipantResponse(p.getParticipantId(), p.getUser().getUsername(), p.getUser().getEmail(), p.getRole(), p.getStatus(), p.getJoinedAt());
           result.add(response);
        }

        return result;
    }

    public List<ParticipantResponse> getInvitations(){
        User user = userService.getCurrentUser();
        List <ParticipantResponse> res = new ArrayList<>();
        List <Participant> participants = participantRepository.findByUserAndStatus(user, ParticipantStatus.PENDING);
        for(Participant p : participants){
            ParticipantResponse response = new ParticipantResponse(p.getParticipantId(), p.getUser().getUsername(), p.getUser().getEmail(), p.getRole(), p.getStatus(), p.getJoinedAt());
            res.add(response);
        }
        return res;
    }

    public void removeParticipant(Long participandId)
    {
        Participant participant = participantRepository.findById(participandId).orElseThrow(() -> new NotFoundException("Participant not found"));
        Trip trip = participant.getTrip();
        User user = userService.getCurrentUser();
        Participant p = participantRepository.findByTripAndUser(trip, user).orElseThrow(() -> new NotFoundException("Participant not found"));

        if(p.getRole() != TripRole.ADMIN)
        {
            throw new ForbiddenException("No permission");
        }
        participantRepository.delete(participant);
    }

    public ParticipantResponse acceptInvitation(Long participandId){
        Participant participant = participantRepository.findById(participandId).orElseThrow(() -> new NotFoundException("Participant not found"));
        User cur_user = userService.getCurrentUser();
        if(!participant.getUser().getUserId().equals(cur_user.getUserId())) {
            throw new BadRequestException("Unable to accept, wrong user");
        }

        if(participant.getStatus() == ParticipantStatus.PENDING){
            participant.setStatus(ParticipantStatus.ACCEPTED);
        }
        else{
            throw new ForbiddenException("Wrong invitation status");
        }
        participantRepository.save(participant);

        return new ParticipantResponse(participant.getParticipantId(), participant.getUser().getUsername(), participant.getUser().getEmail(), participant.getRole(), participant.getStatus(),participant.getJoinedAt());
    }

    public ParticipantResponse declineInvitation(Long participandId){
        Participant participant = participantRepository.findById(participandId).orElseThrow(() -> new NotFoundException("Participant not found"));
        User cur_user = userService.getCurrentUser();
        if(!participant.getUser().getUserId().equals(cur_user.getUserId())) {
            throw new BadRequestException("Wrong user");
        }

        if(participant.getStatus() == ParticipantStatus.PENDING){
            participant.setStatus(ParticipantStatus.DECLINED);
        }
        else{
            throw new BadRequestException("Wrong invitation status");
        }
        participantRepository.save(participant);
        return new ParticipantResponse(participant.getParticipantId(), participant.getUser().getUsername(), participant.getUser().getEmail(), participant.getRole(), participant.getStatus(),participant.getJoinedAt());
    }

    public void leaveTrip(Long participantId){
        Participant participant = participantRepository.findById(participantId).orElseThrow(() -> new NotFoundException("Participant not found"));
        User user = userService.getCurrentUser();
        if(!participant.getUser().getUserId().equals(user.getUserId())){
            throw new ForbiddenException("Wrong user");
        }
        participantRepository.delete(participant);
    }

    public ParticipantResponse changeRole(Long participantId, TripRole role)
    {
        Participant participant = participantRepository.findById(participantId).orElseThrow(() -> new NotFoundException("Participant not found"));
        Trip trip = participant.getTrip();
        User user = userService.getCurrentUser();
        Participant p = participantRepository.findByTripAndUser(trip, user).orElseThrow(() -> new NotFoundException("Participant not found"));
        if(p.getRole() != TripRole.ADMIN)
        {
            throw new ForbiddenException("No permission");
        }
        participant.setRole(role);
        participantRepository.save(participant);
        return new ParticipantResponse(participant.getParticipantId(), participant.getUser().getUsername(), participant.getUser().getEmail(), participant.getRole(), participant.getStatus(),participant.getJoinedAt());
    }

}
