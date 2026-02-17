package com.connect.pairr.model.dto;

import com.connect.pairr.model.enums.ProficiencyLevel;

import java.util.UUID;

public record AddUserSkillRequest(
        UUID skillId,
        ProficiencyLevel proficiency
) {}
