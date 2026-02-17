package com.connect.pairr.controller;

import com.connect.pairr.model.dto.AddCategoryRequest;
import com.connect.pairr.model.dto.AddSkillRequest;
import com.connect.pairr.model.dto.CategoryResponse;
import com.connect.pairr.model.dto.SkillResponse;
import com.connect.pairr.model.dto.UserResponse;
import com.connect.pairr.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public ResponseEntity<String> dashboard() {
        return ResponseEntity.ok("Welcome Admin!");
    }

    @PostMapping("/skills")
    public ResponseEntity<SkillResponse> addSkill(@RequestBody AddSkillRequest request) {
        return ResponseEntity.ok(adminService.addSkill(request));
    }

    @PostMapping("/categories")
    public ResponseEntity<CategoryResponse> addCategory(@RequestBody AddCategoryRequest request) {
        return ResponseEntity.ok(adminService.addCategory(request));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }
}
