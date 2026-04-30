package org.otropets.travelplanner.auth.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import jakarta.transaction.Transactional;
import org.otropets.travelplanner.auth.dto.ForgotPasswordRequest;
import org.otropets.travelplanner.auth.dto.ResetPasswordRequest;
import org.otropets.travelplanner.auth.model.ResetToken;
import org.otropets.travelplanner.auth.model.User;
import org.otropets.travelplanner.auth.repository.PasswordResetRepository;
import org.otropets.travelplanner.auth.repository.UserRepository;
import org.otropets.travelplanner.exception.BadRequestException;
import org.otropets.travelplanner.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EmailService {

    private final UserRepository userRepository;
    private final PasswordResetRepository passwordResetRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${SENDGRID_API_KEY:fake-test-key}")
    private String sendGridApiKey;

    public EmailService(UserRepository userRepository, PasswordResetRepository passwordResetRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordResetRepository = passwordResetRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found"));

        passwordResetRepository.deleteByUser(user);

        String tokenValue = UUID.randomUUID().toString();
        ResetToken token = ResetToken.builder()
                .tokenValue(tokenValue)
                .expireAt(LocalDateTime.now().plusHours(12))
                .user(user)
                .build();
        passwordResetRepository.save(token);

        sendEmail(
                user.getEmail(),
                "TravelPlanner Password Reset",
                "Click to reset your password: https://frontendtravelplanner-production.up.railway.app/reset-password?token=" + tokenValue
        );
    }

    public void resetPassword(ResetPasswordRequest request) {
        ResetToken token = passwordResetRepository.findByTokenValue(request.getToken())
                .orElseThrow(() -> new NotFoundException("Token not found"));

        if (token.getExpireAt().isBefore(LocalDateTime.now())) {
            passwordResetRepository.delete(token);
            throw new BadRequestException("Token expired");
        }

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        passwordResetRepository.delete(token);
    }

    private void sendEmail(String to, String subject, String text) {
        Email from = new Email("sashatropets@gmail.com");
        Email toEmail = new Email(to);
        Content content = new Content("text/plain", text);
        Mail mail = new Mail(from, subject, toEmail, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            if (response.getStatusCode() >= 400) {
                throw new RuntimeException("SendGrid error: " + response.getBody());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }
}