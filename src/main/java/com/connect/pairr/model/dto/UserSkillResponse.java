package com.connect.pairr.model.dto;

import com.connect.pairr.model.entity.Skill;
import com.connect.pairr.model.enums.ProficiencyLevel;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record UserSkillResponse(
        Skill skill,
        BigDecimal rating,
        ProficiencyLevel proficiencyLevel
) {}

