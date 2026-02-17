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
            @RequestParam UUID userId
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
    public void addUserAvailabilities(
            @AuthenticationPrincipal UUID userId,
            @RequestBody List<AddUserAvailabilityRequest> requests
    ) {
        userAvailabilityService.addAvailabilities(userId, requests);
    }
}
