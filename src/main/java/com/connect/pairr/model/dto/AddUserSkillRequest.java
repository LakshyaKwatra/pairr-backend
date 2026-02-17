package com.connect.pairr.model.dto;

import com.connect.pairr.model.enums.ProficiencyLevel;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddUserSkillRequest(
        @NotNull UUID skillId,
        @NotNull ProficiencyLevel proficiency
) {}
