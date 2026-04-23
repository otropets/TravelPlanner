package org.otropets.travelplanner.participant.controller;

import jakarta.validation.Valid;
import org.otropets.travelplanner.participant.service.ParticipantService;
import org.otropets.travelplanner.participant.dto.InviteRequest;
import org.otropets.travelplanner.participant.dto.ParticipantResponse;
import org.otropets.travelplanner.participant.model.TripRole;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/participants")
public class ParticipantController {
    private final ParticipantService service;

    public ParticipantController(ParticipantService participantService) {
        this.service = participantService;
    }

    @PostMapping("/{tripId}/invite")
    public ResponseEntity<ParticipantResponse> inviteRequest(@Valid @RequestBody InviteRequest request, @PathVariable  Long tripId)
    {
        ParticipantResponse response = service.inviteParticipant(tripId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{participantId}")
    public ResponseEntity<ParticipantResponse> getParticipant(@PathVariable Long participantId)
    {
        ParticipantResponse response = service.getParticipant(participantId);

        return ResponseEntity.ok(response);
    }

    @GetMapping({"/trip/{tripId}/list"})
    public ResponseEntity<List<ParticipantResponse>> getParticipantsList(@PathVariable Long tripId){
        List<ParticipantResponse> response = service.getParticipantsList(tripId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping({"/{participantId}"})
    public ResponseEntity<Void> removeParticipant(@PathVariable Long participantId)
    {
        service.removeParticipant(participantId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping({"/{participantId}/accept"})
    public ResponseEntity<ParticipantResponse> acceptInvitation(@PathVariable Long participantId){
        ParticipantResponse response = service.acceptInvitation(participantId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping({"/{participantId}/decline"})
    public ResponseEntity<ParticipantResponse> declineInvitation(@PathVariable Long participantId){
        ParticipantResponse response = service.declineInvitation(participantId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/trip/{participantId}/leave")
    public ResponseEntity<Void> leaveTrip(@PathVariable Long participantId){
        service.leaveTrip(participantId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{participantId}/role")
    public ResponseEntity<ParticipantResponse> changeRole(@PathVariable Long participantId, @RequestBody Map<String, String> body) {
        TripRole role = TripRole.valueOf(body.get("role"));
        return ResponseEntity.ok(service.changeRole(participantId, role));
    }
    @GetMapping("/invitations")
    public ResponseEntity<List<ParticipantResponse>> getInvitations()
    {
        List<ParticipantResponse> invitations =  service.getInvitations();
        return ResponseEntity.ok(invitations);
    }


}
