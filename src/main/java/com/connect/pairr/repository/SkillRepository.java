package com.connect.pairr.repository;

import com.connect.pairr.model.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SkillRepository extends JpaRepository<Skill, UUID>  {
}
