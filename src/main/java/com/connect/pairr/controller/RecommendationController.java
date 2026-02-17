package com.connect.pairr.controller;

import com.connect.pairr.model.dto.Recommendation;
import com.connect.pairr.model.enums.DayType;
import com.connect.pairr.service.RecommendationService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
@Validated
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping
    public ResponseEntity<List<Recommendation>> getRecommendations(
            @AuthenticationPrincipal UUID userId,
            @RequestParam UUID skillId,
            @RequestParam DayType dayType,
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "numberOfCandidates must be at least 1")
            @Max(value = 100, message = "numberOfCandidates must be at most 100")
            int numberOfRecommendations
    ) {
        return ResponseEntity.ok(
                recommendationService.getRecommendations(
                        userId,
                        skillId,
                        dayType,
                        numberOfRecommendations
                )
        );
    }


}

