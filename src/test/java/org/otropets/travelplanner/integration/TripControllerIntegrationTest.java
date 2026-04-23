package org.otropets.travelplanner.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.otropets.travelplanner.auth.model.User;
import org.otropets.travelplanner.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TripControllerIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    private String getToken() throws Exception {
        if (userRepository.findByEmail("test@gmail.com").isEmpty()) {
            User user = User.builder()
                    .username("testuser")
                    .email("test@gmail.com")
                    .password(passwordEncoder.encode("password123"))
                    .firstName("Test")
                    .lastName("User")
                    .enabled(true)
                    .build();
            userRepository.save(user);
        }

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@gmail.com\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // extract token from response
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(response).get("token").asText();
    }

    @Test
    void createTripReturns201() throws Exception {
        mockMvc.perform(
                        post("/api/trips")
                                .header("Authorization", "Bearer " + getToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"tripName\":\"Paris Trip\",\"destination\":\"Paris\",\"startDate\":\"2026-06-01\",\"endDate\":\"2026-06-10\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tripName").value("Paris Trip"))
                .andExpect(jsonPath("$.destination").value("Paris"));
    }


}
