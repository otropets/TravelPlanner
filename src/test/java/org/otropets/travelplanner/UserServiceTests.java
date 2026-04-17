package org.otropets.travelplanner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.otropets.travelplanner.auth.User;
import org.otropets.travelplanner.auth.UserRepository;
import org.otropets.travelplanner.auth.UserService;
import org.otropets.travelplanner.auth.dto.AuthResponse;
import org.otropets.travelplanner.auth.dto.LoginRequest;
import org.otropets.travelplanner.auth.dto.RegisterRequest;
import org.otropets.travelplanner.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserService userService;
    @Mock
    JwtService jwtService;
    @Mock
    PasswordEncoder passwordEncoder;

    @Test
    void successfulRegister(){
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashed_password");
        when(jwtService.generateToken(any())).thenReturn("token");

        RegisterRequest registerRequest = new RegisterRequest(
                "john", "password", "john@gmail.com", "John", "Abc");

        AuthResponse response = userService.register(registerRequest);

        assertNotNull(response);
        assertEquals("john", response.getUsername());
        assertEquals("john@gmail.com", response.getEmail());
        assertEquals("token", response.getToken());
    }
    @Test
    void unsuccessfulRegisterUsernameAlreadyTaken(){
        when(userRepository.existsByUsername(any())).thenReturn(true);

        RegisterRequest registerRequest = new RegisterRequest(
                "john", "password", "john@gmail.com", "John", "Abc");

        assertThrows(RuntimeException.class, () -> userService.register(registerRequest));
    }
    @Test
    void unsuccessfulRegisterEmailAlreadyTaken(){
        when(userRepository.existsByEmail(any())).thenReturn(true);

        RegisterRequest registerRequest = new RegisterRequest(
                "john", "password", "john@gmail.com", "John", "Abc");

        assertThrows(RuntimeException.class, () -> userService.register(registerRequest));
    }
    @Test
    void successfulLogin()
    {
        User mockUser = User.builder().username("john").firstName("John").lastName("Abc").password("hashed_password").email("john@gmail.com").build();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(jwtService.generateToken(any())).thenReturn("token");

        LoginRequest loginRequest = new LoginRequest("john@gmail.com", "password");
        AuthResponse authResponse = userService.login(loginRequest);

        assertNotNull(authResponse);
        assertEquals("john@gmail.com", authResponse.getEmail());
        assertEquals("john",authResponse.getUsername());
        assertEquals("token", authResponse.getToken());
    }
    @Test
    void unsuccessfulLoginUserNotFound()
    {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        LoginRequest loginRequest = new LoginRequest("john@gmail.com", "passsword");

        assertThrows(RuntimeException.class, () -> userService.login(loginRequest));
    }
    @Test
    void unsuccessfulLoginWrongPassword()
    {
        User mockUser = User.builder().username("john").firstName("John").lastName("Abc").password("hashed_password").email("john@gmail.com").build();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        LoginRequest loginRequest = new LoginRequest("john@email.com", "password");

        assertThrows(RuntimeException.class, ()-> userService.login(loginRequest));

    }

}
