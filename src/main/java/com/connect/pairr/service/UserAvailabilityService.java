package com.connect.pairr.service;

import com.connect.pairr.model.dto.AddUserAvailabilityRequest;
import com.connect.pairr.model.entity.User;
import com.connect.pairr.model.entity.UserAvailability;
import com.connect.pairr.model.enums.DayType;
import com.connect.pairr.exception.UserNotFoundException;
import com.connect.pairr.repository.UserAvailabilityRepository;
import com.connect.pairr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
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

        if (requests.isEmpty()) return List.of();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        List<UserAvailability> newAvailabilities = new ArrayList<>();
        for (AddUserAvailabilityRequest req : requests) {
            UserAvailability ua = new UserAvailability();
            ua.setUser(user); // assuming you have a userId field
            ua.setDayType(req.dayType());
            ua.setStartTime(req.startTime());
            ua.setEndTime(req.endTime());
            newAvailabilities.add(ua);
        }

        // Fetch existing availabilities for the user
        List<UserAvailability> existing = userAvailabilityRepository.findAllByUserId(userId);

        // Combine old + new and sort by dayType + startTime
        List<UserAvailability> combined = new ArrayList<>(existing);
        combined.addAll(newAvailabilities);
        combined.sort(Comparator
                .comparing(UserAvailability::getDayType)
                .thenComparing(UserAvailability::getStartTime));

        // Merge intervals day-wise
        List<UserAvailability> merged = new ArrayList<>();
        Map<DayType, List<UserAvailability>> dayMap = new LinkedHashMap<>();
        for (UserAvailability ua : combined) {
            dayMap.computeIfAbsent(ua.getDayType(), k -> new ArrayList<>()).add(ua);
        }

        for (var entry : dayMap.entrySet()) {
            List<UserAvailability> dayList = entry.getValue();
            UserAvailability prev = dayList.get(0);
            for (int i = 1; i < dayList.size(); i++) {
                UserAvailability current = dayList.get(i);
                if (!current.getStartTime().isAfter(prev.getEndTime())) {
                    prev.setEndTime(max(prev.getEndTime(), current.getEndTime()));
                } else {
                    merged.add(prev);
                    prev = current;
                }
            }
            merged.add(prev);
        }

        // Build fresh entities from merged intervals to avoid Hibernate
        // "deleted instance passed to merge" when reusing managed entities
        List<UserAvailability> freshMerged = new ArrayList<>();
        for (UserAvailability ua : merged) {
            UserAvailability fresh = new UserAvailability();
            fresh.setUser(user);
            fresh.setDayType(ua.getDayType());
            fresh.setStartTime(ua.getStartTime());
            fresh.setEndTime(ua.getEndTime());
            freshMerged.add(fresh);
        }

        // Delete old intervals and save merged
        userAvailabilityRepository.deleteAll(existing);
        userAvailabilityRepository.flush();
        return userAvailabilityRepository.saveAll(freshMerged);
    }

    private record Interval(
            DayType dayType,
            LocalTime start,
            LocalTime end
    ) {}

    private LocalTime max(LocalTime t1, LocalTime t2) {
        return t1.isAfter(t2) ? t1 : t2;
    }

}
