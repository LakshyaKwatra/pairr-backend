package com.connect.pairr.exception;

import java.util.UUID;

public class UserSkillAlreadyExistsException extends RuntimeException {
    public UserSkillAlreadyExistsException(UUID skillId) {
        super("User already has this skill: " + skillId);
    }
}
