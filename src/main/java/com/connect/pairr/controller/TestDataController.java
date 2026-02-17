package com.connect.pairr.controller;

import com.connect.pairr.model.dto.CreateUserDto;
import com.connect.pairr.model.dto.UserResponse;
import com.connect.pairr.service.TestDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestDataController {

    private final TestDataService testDataService;

    @PostMapping("/users")
    public UserResponse createUser(@RequestBody CreateUserDto dto) {
        return testDataService.createUser(dto);
    }
}
