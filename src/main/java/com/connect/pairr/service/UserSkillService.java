package com.connect.pairr.service;

import com.connect.pairr.model.dto.AddUserSkillRequest;
import com.connect.pairr.model.entity.Skill;
import com.connect.pairr.model.entity.User;
import com.connect.pairr.model.entity.UserSkill;
import com.connect.pairr.exception.SkillNotFoundException;
import com.connect.pairr.exception.UserNotFoundException;
import com.connect.pairr.exception.UserSkillAlreadyExistsException;
import com.connect.pairr.repository.SkillRepository;
import com.connect.pairr.repository.UserRepository;
import com.connect.pairr.repository.UserSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserSkillService {

    private final UserRepository userRepository;
    private final UserSkillRepository userSkillRepository;
    private final SkillRepository skillRepository;

    public List<UserSkill> getUserSkills(UUID userId) {
        return userSkillRepository.findAllByUserId(userId);
    }

    public void addSkills(UUID userId, List<AddUserSkillRequest> requests) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Fetch all existing user skills in ONE query
        List<UserSkill> existingUserSkills = userSkillRepository.findAllByUserId(userId);


        // Extract existing skill IDs into a Set
        Set<UUID> existingSkillIds = existingUserSkills.stream()
                .map(us -> us.getSkill().getId())
                .collect(Collectors.toSet());

        // Fetch all requested skills in ONE query
        List<UUID> requestedSkillIds = requests.stream()
                .map(AddUserSkillRequest::skillId)
                .toList();

        List<Skill> skills = skillRepository.findAllById(requestedSkillIds);

        Map<UUID, Skill> skillMap = skills.stream()
                .collect(Collectors.toMap(Skill::getId, s -> s));

        // Filter in memory + build new entities
        List<UserSkill> toSave = new ArrayList<>();

        for (AddUserSkillRequest request : requests) {

            if (existingSkillIds.contains(request.skillId())) {
                throw new UserSkillAlreadyExistsException(request.skillId());
            }

            Skill skill = skillMap.get(request.skillId());

            if (skill == null) {
                throw new SkillNotFoundException(request.skillId());
            }

            UserSkill userSkill = new UserSkill();
            userSkill.setUser(user);
            userSkill.setSkill(skill);
            userSkill.setProficiency(request.proficiency());

            toSave.add(userSkill);
        }

        // Bulk save
        userSkillRepository.saveAll(toSave);
    }
}

