package com.connect.pairr.controller;

import com.connect.pairr.model.dto.AddUserAvailabilityRequest;
import com.connect.pairr.model.dto.UserAvailabilityResponse;
import com.connect.pairr.model.entity.UserAvailability;
import com.connect.pairr.service.UserAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user/availability")
@RequiredArgsConstructor
public class UserAvailabilityController {

    private final UserAvailabilityService userAvailabilityService;

    @GetMapping
    public ResponseEntity<List<UserAvailabilityResponse>> getUserAvailabilities(
            @AuthenticationPrincipal UUID userId
    ) {
        List<UserAvailability> availabilities = userAvailabilityService.getUserAvailabilities(userId);

        List<UserAvailabilityResponse> response = availabilities.stream()
                .map(ua -> UserAvailabilityResponse.builder()
                        .dayType(ua.getDayType())
                        .startTime(ua.getStartTime())
                        .endTime(ua.getEndTime())
                        .build()
                ).toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<List<UserAvailabilityResponse>> addUserAvailabilities(
            @AuthenticationPrincipal UUID userId,
            @RequestBody List<AddUserAvailabilityRequest> requests
    ) {
        List<UserAvailability> availabilities = userAvailabilityService.addAvailabilities(userId, requests);

        List<UserAvailabilityResponse> response = availabilities.stream()
                .map(ua -> UserAvailabilityResponse.builder()
                        .dayType(ua.getDayType())
                        .startTime(ua.getStartTime())
                        .endTime(ua.getEndTime())
                        .build()
                ).toList();

        return ResponseEntity.ok(response);
    }
}
