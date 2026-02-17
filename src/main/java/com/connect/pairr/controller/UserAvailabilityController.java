package com.connect.pairr.controller;

import com.connect.pairr.mapper.UserAvailabilityMapper;
import com.connect.pairr.model.dto.AddUserAvailabilityRequest;
import com.connect.pairr.model.dto.UserAvailabilityResponse;
import com.connect.pairr.service.UserAvailabilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user/availability")
@RequiredArgsConstructor
@Tag(name = "User Availability", description = "Manage your availability windows")
public class UserAvailabilityController {

    private final UserAvailabilityService userAvailabilityService;

    @GetMapping
    @Operation(summary = "Get your availability")
    public ResponseEntity<List<UserAvailabilityResponse>> getUserAvailabilities(
            @AuthenticationPrincipal UUID userId
    ) {
        List<UserAvailabilityResponse> response = userAvailabilityService.getUserAvailabilities(userId).stream()
                .map(UserAvailabilityMapper::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Set availability windows", description = "Replaces all existing availability with the provided list. Send an empty list to clear.")
    public ResponseEntity<List<UserAvailabilityResponse>> addUserAvailabilities(
            @AuthenticationPrincipal UUID userId,
            @RequestBody @Valid List<AddUserAvailabilityRequest> requests
    ) {
        List<UserAvailabilityResponse> response = userAvailabilityService.addAvailabilities(userId, requests).stream()
                .map(UserAvailabilityMapper::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }
}
