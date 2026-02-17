package com.connect.pairr.service;

import com.connect.pairr.model.dto.CreateUserDto;
import com.connect.pairr.model.dto.UserResponse;
import com.connect.pairr.model.entity.User;
import com.connect.pairr.model.enums.Role;
import com.connect.pairr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TestDataService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponse createUser(CreateUserDto dto) {
        User user = new User();
        user.setDisplayName(dto.displayName());
        user.setEmail(dto.email());
        user.setPassword(dto.password());
        user.setRole(Role.USER);
        user = userRepository.save(user);
        return toUserResponse(user);
    }

    private static UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getRole(),
                user.getOverallRating(),
                user.getCreatedAt()
        );
    }
}
