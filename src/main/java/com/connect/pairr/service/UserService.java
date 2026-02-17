package com.connect.pairr.service;

import com.connect.pairr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Checks if a user exists by ID.
     * Results are cached to reduce database queries.
     *
     * @param userId User ID to check
     * @return true if user exists, false otherwise
     */
    @Cacheable(value = "userExistence", key = "#userId")
    public boolean userExists(UUID userId) {
        return userRepository.existsById(userId);
    }
}
