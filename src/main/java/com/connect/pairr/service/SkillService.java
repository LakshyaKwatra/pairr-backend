package com.connect.pairr.service;

import com.connect.pairr.model.dto.SkillResponse;
import com.connect.pairr.model.entity.Skill;
import com.connect.pairr.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;

    public List<SkillResponse> getAllSkills() {
        return skillRepository.findAll().stream()
                .map(SkillService::toSkillResponse)
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
}
