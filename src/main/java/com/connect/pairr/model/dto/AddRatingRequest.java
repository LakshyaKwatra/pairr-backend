package com.connect.pairr.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record AddRatingRequest(
        @NotNull UUID toUserId,
        @NotNull UUID skillId,
        @NotNull @Min(1) @Max(5) Integer rating,
        @Size(max = 500) String feedback
) {}
