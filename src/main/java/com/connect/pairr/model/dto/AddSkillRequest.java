package com.connect.pairr.model.dto;

import java.util.UUID;

public record AddSkillRequest(
        String name,
        UUID categoryId
) {}
