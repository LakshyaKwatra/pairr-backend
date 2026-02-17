package com.connect.pairr.model.dto;

import com.connect.pairr.model.enums.DayType;

import java.time.LocalTime;

public record AddUserAvailabilityRequest(
        DayType dayType,
        LocalTime startTime,
        LocalTime endTime
) {}