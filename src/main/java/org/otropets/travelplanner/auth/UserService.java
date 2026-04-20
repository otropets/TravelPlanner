package org.otropets.travelplanner.auth;

import org.otropets.travelplanner.auth.dto.AuthResponse;
import org.otropets.travelplanner.auth.dto.LoginRequest;
import org.otropets.travelplanner.auth.dto.RegisterRequest;
import org.otropets.travelplanner.exception.BadRequestException;
import org.otropets.travelplanner.exception.ConflictException;
import org.otropets.travelplanner.exception.NotFoundException;
import org.otropets.travelplanner.security.JwtService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse register(RegisterRequest registerRequest)
    {
        if(userRepository.existsByEmail(registerRequest.getEmail()) || userRepository.existsByUsername(registerRequest.getUsername()))
        {
            throw new ConflictException("Email or username already taken");
        }

        String hashedPassword = passwordEncoder.encode(registerRequest.getPassword());
        User user = User.builder().username(registerRequest.getUsername()).email(registerRequest.getEmail()).password(hashedPassword).firstName(registerRequest.getFirstName()).lastName(registerRequest.getLastName()).build();
        userRepository.save(user);
        String token = jwtService.generateToken(registerRequest.getEmail());
        return new AuthResponse(user.getUsername(), user.getEmail(), token);
    }

    public AuthResponse login(LoginRequest loginRequest)
    {
      User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() ->new NotFoundException("User not found"));
      if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()))
      {
          throw new BadRequestException("Invalid password");
      }
      String token = jwtService.generateToken(user.getEmail());
      return new AuthResponse(user.getUsername(), user.getEmail(), token);
    }

    public User getCurrentUser(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
    }

}
