package com.connect.pairr.model.dto;

import com.connect.pairr.model.enums.DayType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record AddUserAvailabilityRequest(
        @NotNull DayType dayType,
        @NotNull LocalTime startTime,
        @NotNull LocalTime endTime
) {
    @AssertTrue(message = "startTime must be before endTime")
    private boolean isValidTimeRange() {
        // null-guard needed because @AssertTrue may run before @NotNull
        if (startTime == null || endTime == null) return true;
        return startTime.isBefore(endTime);
    }
}