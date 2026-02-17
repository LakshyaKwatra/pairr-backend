package com.connect.pairr.controller;

import com.connect.pairr.model.dto.AddUserSkillRequest;
import com.connect.pairr.model.dto.UserSkillResponse;
import com.connect.pairr.model.entity.UserSkill;
import com.connect.pairr.service.UserSkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user/skills")
@RequiredArgsConstructor
public class UserSkillController {

    private final UserSkillService userSkillService;

    @PostMapping
    public void addUserSkills(
            @AuthenticationPrincipal UUID userId,
            @RequestBody List<AddUserSkillRequest> requests
    ) {
        userSkillService.addSkills(userId, requests);
    }

    @GetMapping
    public ResponseEntity<List<UserSkillResponse>> getUserSkills(@AuthenticationPrincipal UUID userId) {

        List<UserSkill> userSkills = userSkillService.getUserSkills(userId);

        List<UserSkillResponse> response = userSkills.stream()
                .map(us -> UserSkillResponse.builder()
                        .skill(us.getSkill())
                        .rating(us.getRating())
                        .proficiencyLevel(us.getProficiency())
                        .build()
                ).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
