package org.otropets.travelplanner.auth.repository;

import org.otropets.travelplanner.auth.model.ResetToken;
import org.otropets.travelplanner.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface PasswordResetRepository extends JpaRepository<ResetToken, Long> {

    void deleteByUser(User user);

    Optional<ResetToken> findByTokenValue(String tokenValue);
}
