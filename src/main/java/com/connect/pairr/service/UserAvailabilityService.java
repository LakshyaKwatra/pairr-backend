package com.connect.pairr.service;

import com.connect.pairr.model.dto.AddUserAvailabilityRequest;
import com.connect.pairr.model.entity.User;
import com.connect.pairr.model.entity.UserAvailability;
import com.connect.pairr.exception.UserNotFoundException;
import com.connect.pairr.repository.UserAvailabilityRepository;
import com.connect.pairr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserAvailabilityService {

    private final UserAvailabilityRepository userAvailabilityRepository;
    private final UserRepository userRepository;

    public List<UserAvailability> getUserAvailabilities(UUID userId) {
        return userAvailabilityRepository.findAllByUserId(userId);
    }

    @Transactional
    public List<UserAvailability> addAvailabilities(UUID userId, List<AddUserAvailabilityRequest> requests) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Delete all existing availabilities and replace with the new set
        userAvailabilityRepository.deleteAllByUserId(userId);
        userAvailabilityRepository.flush();

        if (requests.isEmpty()) return List.of();

        List<UserAvailability> newAvailabilities = new ArrayList<>();
        for (AddUserAvailabilityRequest req : requests) {
            UserAvailability ua = new UserAvailability();
            ua.setUser(user);
            ua.setDayType(req.dayType());
            ua.setStartTime(req.startTime());
            ua.setEndTime(req.endTime());
            newAvailabilities.add(ua);
        }

        return userAvailabilityRepository.saveAll(newAvailabilities);
    }

}
