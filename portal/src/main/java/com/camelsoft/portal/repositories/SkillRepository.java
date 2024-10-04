package com.camelsoft.portal.repositories;

import com.camelsoft.portal.models.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Integer> {

    Page<Skill> findBySkill(String skill,Pageable pageable);
}
