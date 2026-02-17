package com.connect.pairr.controller;

import com.connect.pairr.model.dto.AddRatingRequest;
import com.connect.pairr.model.dto.RatingResponse;
import com.connect.pairr.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<RatingResponse> submitRating(
            @AuthenticationPrincipal UUID userId,
            @RequestBody @Valid AddRatingRequest request
    ) {
        return ResponseEntity.ok(ratingService.submitRating(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<RatingResponse>> getRatings(
            @RequestParam UUID userId,
            @RequestParam(required = false) UUID skillId
    ) {
        if (skillId != null) {
            return ResponseEntity.ok(ratingService.getRatingsForUserSkill(userId, skillId));
        }
        return ResponseEntity.ok(ratingService.getRatingsForUser(userId));
    }
}
