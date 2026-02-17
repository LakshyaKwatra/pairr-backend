package com.connect.pairr.service;

import com.connect.pairr.model.dto.AddCategoryRequest;
import com.connect.pairr.model.dto.AddSkillRequest;
import com.connect.pairr.model.dto.CategoryResponse;
import com.connect.pairr.model.dto.SkillResponse;
import com.connect.pairr.model.dto.UserResponse;
import com.connect.pairr.model.entity.Category;
import com.connect.pairr.model.entity.Skill;
import com.connect.pairr.model.entity.User;
import com.connect.pairr.repository.CategoryRepository;
import com.connect.pairr.repository.SkillRepository;
import com.connect.pairr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final SkillRepository skillRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public SkillResponse addSkill(AddSkillRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new NoSuchElementException("No category present with provided id"));

        Skill skill = Skill.builder()
                .category(category)
                .name(request.name())
                .build();

        skill = skillRepository.save(skill);
        return toSkillResponse(skill);
    }

    public CategoryResponse addCategory(AddCategoryRequest request) {
        Category category = Category.builder()
                .name(request.name())
                .build();

        category = categoryRepository.save(category);
        return new CategoryResponse(category.getId(), category.getName());
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(AdminService::toUserResponse)
                .toList();
    }

    private static SkillResponse toSkillResponse(Skill skill) {
        return new SkillResponse(
                skill.getId(),
                skill.getName(),
                skill.getCategory().getId(),
                skill.getCategory().getName()
        );
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
