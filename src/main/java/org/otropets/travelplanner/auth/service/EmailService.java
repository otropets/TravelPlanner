package org.otropets.travelplanner.auth.service;

import jakarta.transaction.Transactional;
import org.otropets.travelplanner.auth.dto.ForgotPasswordRequest;
import org.otropets.travelplanner.auth.dto.ResetPasswordRequest;
import org.otropets.travelplanner.auth.model.ResetToken;
import org.otropets.travelplanner.auth.model.User;
import org.otropets.travelplanner.auth.repository.PasswordResetRepository;
import org.otropets.travelplanner.auth.repository.UserRepository;
import org.otropets.travelplanner.exception.BadRequestException;
import org.otropets.travelplanner.exception.NotFoundException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EmailService {

    private final UserRepository userRepository;
    private final PasswordResetRepository passwordResetRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    public EmailService(UserRepository userRepository, PasswordResetRepository passwordResetRepository, JavaMailSender mailSender, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordResetRepository = passwordResetRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request){
        // find a user
        // create a token and save to db
        // create a message to user and send it

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new NotFoundException("User not found"));
        passwordResetRepository.deleteByUser(user);

        String tokenValue = UUID.randomUUID().toString();
        ResetToken token = ResetToken.builder().tokenValue(tokenValue).expireAt(LocalDateTime.now().plusHours(12)).user(user).build();
        passwordResetRepository.save(token);

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("sashatropets@gmail.com");
        message.setTo(user.getEmail());
        message.setSubject("TravelPlanner password Reset");
        message.setText("Click to reset your password: " +
                "https://frontendtravelplanner-production.up.railway.app/reset-password?token=" + tokenValue);

        /*
        message.setText("Click to reset your password: " +
                "http://localhost:5173/reset-password?token=" + tokenValue);
        */
        mailSender.send(message);
    }

    public void resetPassword(ResetPasswordRequest request)
    {
        // find by Token in PasswordResetRepo
        // check if not expired
            // exception
        // set a new password (HASHED!) to userRepository
        // save new user
        // delete token

        ResetToken token = passwordResetRepository.findByTokenValue(request.getToken()).orElseThrow(() -> new NotFoundException("Token not found"));

        if(token.getExpireAt().isBefore(LocalDateTime.now()))
        {
            passwordResetRepository.delete(token);
            throw new BadRequestException("Token expired");
        }
        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        passwordResetRepository.delete(token);
    }

}
