package com.abhi.BloggingSite_Backend.Services;

import com.abhi.BloggingSite_Backend.Exception.InvalidCredentialsException;
import com.abhi.BloggingSite_Backend.Exception.UsernameAlreadyTakenException;
import com.abhi.BloggingSite_Backend.Model.Users;
import com.abhi.BloggingSite_Backend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private JWTService jwtService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public Users registerUser(Users user) throws UsernameAlreadyTakenException {

        if (repo.existsByUsername(user.getUsername())) {
            logger.warn("Registration failed: Username '{}' is already taken", user.getUsername());
            throw new UsernameAlreadyTakenException("Username already taken");
        }

        user.setPassword(encoder.encode(user.getPassword()));
        Users savedUser = repo.save(user);
        logger.info("User '{}' registered successfully", savedUser.getUsername());
        return savedUser;
    }

    public String verify(Users user) throws InvalidCredentialsException {
        try {
            Users storedUser = repo.findByUsername(user.getUsername());

            if (storedUser == null || !encoder.matches(user.getPassword(), storedUser.getPassword())) {
                logger.error("Invalid credentials for username '{}'", user.getUsername());
                throw new InvalidCredentialsException("Invalid credentials");
            }

            return jwtService.generateToken(user.getUsername());
        } catch (Exception e) {
            logger.error("Error during authentication for username '{}'", user.getUsername());
            throw new InvalidCredentialsException("Invalid credentials provided.");
        }
    }

    public Users findByUsername(String username) {
        return repo.findByUsername(username);
    }

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        return jwtService.generateToken(username);
    }

    public String extractUsername(String jwtToken) {
        try {
            if (jwtToken.startsWith("Bearer ")) {
                jwtToken = jwtToken.substring(7);
            }

            return jwtService.extractUsername(jwtToken);
        } catch (Exception e) {
            logger.error("Token extraction failed: {}", e.getMessage());
            throw new RuntimeException("Token extraction failed: " + e.getMessage());
        }
    }
}
