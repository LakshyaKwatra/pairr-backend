package com.connect.pairr.controller;

import com.connect.pairr.mapper.UserSkillMapper;
import com.connect.pairr.model.dto.AddUserSkillRequest;
import com.connect.pairr.model.dto.UserSkillResponse;
import com.connect.pairr.service.UserSkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

        List<UserSkillResponse> response = userSkillService.getUserSkills(userId).stream()
                .map(UserSkillMapper::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }
}
