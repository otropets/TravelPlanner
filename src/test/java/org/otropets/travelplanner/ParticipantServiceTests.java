package org.otropets.travelplanner;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.otropets.travelplanner.auth.User;
import org.otropets.travelplanner.auth.UserRepository;
import org.otropets.travelplanner.auth.UserService;
import org.otropets.travelplanner.participant.*;
import org.otropets.travelplanner.participant.dto.InviteRequest;
import org.otropets.travelplanner.participant.dto.ParticipantResponse;
import org.otropets.travelplanner.trip.Trip;
import org.otropets.travelplanner.trip.TripRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class ParticipantServiceTests {
    @Mock
    UserService userService;
    @Mock
    TripRepository tripRepository;
    @Mock
    ParticipantRepository participantRepository;
    @Mock
    UserRepository userRepository;

    @InjectMocks
    ParticipantService participantService;

    @Test
    void inviteParticipantSuccessfully(){
        User user = User.builder().username("john").password("password123").email("john@gmail.com").firstName("john").lastName("smith").build();
        User user_to_invite = User.builder().username("mark").password("password123").email("mark@gmail.com").firstName("mark").lastName("mccalister").build();
        Trip trip = Trip.builder().tripId(2L).tripName("Trip to Paris").destination("Paris").startDate(LocalDate.of(2026,1,1)).endDate(LocalDate.of(2026,1,4)).createdBy(user).build();
        Participant participant = Participant.builder().participantId(3L).role(TripRole.ADMIN).trip(trip).user(user).status(ParticipantStatus.ACCEPTED).build();
        when(userService.getCurrentUser()).thenReturn(user);
        when(tripRepository.findById(any())).thenReturn(Optional.ofNullable(trip));
        when(participantRepository.findByTripAndUser(trip, user)).thenReturn(Optional.ofNullable(participant));
        when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(user_to_invite));

        InviteRequest request = new InviteRequest("john@gmail.com", TripRole.PARTICIPANT);
        ParticipantResponse response = participantService.inviteParticipant(1L, request);

        assertNotNull(response);
        assertEquals(TripRole.PARTICIPANT, response.getRole());
        assertEquals(ParticipantStatus.PENDING, response.getStatus());
    }

    @Test
    void inviteParticipantUnauthorized() {
        User user = User.builder().userId(1L).username("john").password("password123").email("john@gmail.com").firstName("john").lastName("smith").build();
        Trip trip = Trip.builder().tripId(1L).tripName("Trip to Paris").destination("Paris").startDate(LocalDate.of(2026,1,1)).endDate(LocalDate.of(2026,1,4)).createdBy(user).build();
        Participant participant = Participant.builder().participantId(1L).role(TripRole.PARTICIPANT).trip(trip).user(user).status(ParticipantStatus.ACCEPTED).build();

        when(userService.getCurrentUser()).thenReturn(user);
        when(tripRepository.findById(any())).thenReturn(Optional.of(trip));
        when(participantRepository.findByTripAndUser(any(), any())).thenReturn(Optional.of(participant));

        InviteRequest request = new InviteRequest("mark@gmail.com", TripRole.PARTICIPANT);
        assertThrows(RuntimeException.class, () -> participantService.inviteParticipant(1L, request));
    }

    @Test
    void inviteParticipantTripNotFound() {
        when(tripRepository.findById(any())).thenReturn(Optional.empty());
        InviteRequest request = new InviteRequest("mark@gmail.com", TripRole.PARTICIPANT);
        assertThrows(RuntimeException.class, () -> participantService.inviteParticipant(1L, request));
    }

    @Test
    void getParticipantSuccessfully() {
        User user = User.builder().userId(1L).username("john").email("john@gmail.com").password("password123").firstName("john").lastName("smith").build();
        Trip trip = Trip.builder().tripId(1L).tripName("Trip to Paris").destination("Paris").startDate(LocalDate.of(2026,1,1)).endDate(LocalDate.of(2026,1,4)).createdBy(user).build();
        Participant participant = Participant.builder().participantId(1L).role(TripRole.PARTICIPANT).trip(trip).user(user).status(ParticipantStatus.ACCEPTED).build();

        when(participantRepository.findById(1L)).thenReturn(Optional.of(participant));

        ParticipantResponse response = participantService.getParticipant(1L);

        assertNotNull(response);
        assertEquals("john", response.getUsername());
        assertEquals(TripRole.PARTICIPANT, response.getRole());
    }

    @Test
    void getParticipantNotFound() {
        when(participantRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> participantService.getParticipant(1L));
    }

    @Test
    void acceptInvitationSuccessfully() {
        User user = User.builder().userId(1L).username("john").email("john@gmail.com").password("password123").firstName("john").lastName("smith").build();
        Trip trip = Trip.builder().tripId(1L).tripName("Trip to Paris").destination("Paris").startDate(LocalDate.of(2026,1,1)).endDate(LocalDate.of(2026,1,4)).createdBy(user).build();
        Participant participant = Participant.builder().participantId(1L).role(TripRole.PARTICIPANT).trip(trip).user(user).status(ParticipantStatus.PENDING).build();

        when(participantRepository.findById(1L)).thenReturn(Optional.of(participant));
        when(userService.getCurrentUser()).thenReturn(user);
        when(participantRepository.save(any(Participant.class))).thenAnswer(i -> i.getArgument(0));

        ParticipantResponse response = participantService.acceptInvitation(1L);

        assertNotNull(response);
        assertEquals(ParticipantStatus.ACCEPTED, response.getStatus());
    }

    @Test
    void acceptInvitationWrongUser() {
        User user = User.builder().userId(1L).username("john").email("john@gmail.com").password("password123").firstName("john").lastName("smith").build();
        User otherUser = User.builder().userId(2L).username("mark").email("mark@gmail.com").password("password123").firstName("mark").lastName("smith").build();
        Trip trip = Trip.builder().tripId(1L).tripName("Trip to Paris").destination("Paris").startDate(LocalDate.of(2026,1,1)).endDate(LocalDate.of(2026,1,4)).createdBy(user).build();
        Participant participant = Participant.builder().participantId(1L).role(TripRole.PARTICIPANT).trip(trip).user(user).status(ParticipantStatus.PENDING).build();

        when(participantRepository.findById(1L)).thenReturn(Optional.of(participant));
        when(userService.getCurrentUser()).thenReturn(otherUser);

        assertThrows(RuntimeException.class, () -> participantService.acceptInvitation(1L));
    }

    @Test
    void acceptInvitationWrongStatus() {
        User user = User.builder().userId(1L).username("john").email("john@gmail.com").password("password123").firstName("john").lastName("smith").build();
        Trip trip = Trip.builder().tripId(1L).tripName("Trip to Paris").destination("Paris").startDate(LocalDate.of(2026,1,1)).endDate(LocalDate.of(2026,1,4)).createdBy(user).build();
        Participant participant = Participant.builder().participantId(1L).role(TripRole.PARTICIPANT).trip(trip).user(user).status(ParticipantStatus.ACCEPTED).build();

        when(participantRepository.findById(1L)).thenReturn(Optional.of(participant));
        when(userService.getCurrentUser()).thenReturn(user);

        assertThrows(RuntimeException.class, () -> participantService.acceptInvitation(1L));
    }

    @Test
    void declineInvitationSuccessfully() {
        User user = User.builder().userId(1L).username("john").email("john@gmail.com").password("password123").firstName("john").lastName("smith").build();
        Trip trip = Trip.builder().tripId(1L).tripName("Trip to Paris").destination("Paris").startDate(LocalDate.of(2026,1,1)).endDate(LocalDate.of(2026,1,4)).createdBy(user).build();
        Participant participant = Participant.builder().participantId(1L).role(TripRole.PARTICIPANT).trip(trip).user(user).status(ParticipantStatus.PENDING).build();

        when(participantRepository.findById(1L)).thenReturn(Optional.of(participant));
        when(userService.getCurrentUser()).thenReturn(user);
        when(participantRepository.save(any(Participant.class))).thenAnswer(i -> i.getArgument(0));

        ParticipantResponse response = participantService.declineInvitation(1L);

        assertNotNull(response);
        assertEquals(ParticipantStatus.DECLINED, response.getStatus());
    }

    @Test
    void removeParticipantSuccessfully() {
        User user = User.builder().userId(1L).username("john").email("john@gmail.com").password("password123").firstName("john").lastName("smith").build();
        Trip trip = Trip.builder().tripId(1L).tripName("Trip to Paris").destination("Paris").startDate(LocalDate.of(2026,1,1)).endDate(LocalDate.of(2026,1,4)).createdBy(user).build();
        Participant adminParticipant = Participant.builder().participantId(1L).role(TripRole.ADMIN).trip(trip).user(user).status(ParticipantStatus.ACCEPTED).build();
        Participant targetParticipant = Participant.builder().participantId(2L).role(TripRole.PARTICIPANT).trip(trip).user(user).status(ParticipantStatus.ACCEPTED).build();

        when(participantRepository.findById(2L)).thenReturn(Optional.of(targetParticipant));
        when(userService.getCurrentUser()).thenReturn(user);
        when(participantRepository.findByTripAndUser(any(), any())).thenReturn(Optional.of(adminParticipant));

        assertDoesNotThrow(() -> participantService.removeParticipant(2L));
    }

    @Test
    void leaveTripSuccessfully() {
        User user = User.builder().userId(1L).username("john").email("john@gmail.com").password("password123").firstName("john").lastName("smith").build();
        Trip trip = Trip.builder().tripId(1L).tripName("Trip to Paris").destination("Paris").startDate(LocalDate.of(2026,1,1)).endDate(LocalDate.of(2026,1,4)).createdBy(user).build();
        Participant participant = Participant.builder().participantId(1L).role(TripRole.PARTICIPANT).trip(trip).user(user).status(ParticipantStatus.ACCEPTED).build();

        when(participantRepository.findById(1L)).thenReturn(Optional.of(participant));
        when(userService.getCurrentUser()).thenReturn(user);

        assertDoesNotThrow(() -> participantService.leaveTrip(1L));
    }

    @Test
    void changeRoleSuccessfully() {
        User user = User.builder().userId(1L).username("john").email("john@gmail.com").password("password123").firstName("john").lastName("smith").build();
        Trip trip = Trip.builder().tripId(1L).tripName("Trip to Paris").destination("Paris").startDate(LocalDate.of(2026,1,1)).endDate(LocalDate.of(2026,1,4)).createdBy(user).build();
        Participant adminParticipant = Participant.builder().participantId(1L).role(TripRole.ADMIN).trip(trip).user(user).status(ParticipantStatus.ACCEPTED).build();
        Participant targetParticipant = Participant.builder().participantId(2L).role(TripRole.PARTICIPANT).trip(trip).user(user).status(ParticipantStatus.ACCEPTED).build();

        when(participantRepository.findById(2L)).thenReturn(Optional.of(targetParticipant));
        when(userService.getCurrentUser()).thenReturn(user);
        when(participantRepository.findByTripAndUser(any(), any())).thenReturn(Optional.of(adminParticipant));
        when(participantRepository.save(any(Participant.class))).thenAnswer(i -> i.getArgument(0));

        ParticipantResponse response = participantService.changeRole(2L, TripRole.GUEST);

        assertNotNull(response);
        assertEquals(TripRole.GUEST, response.getRole());
    }

}
