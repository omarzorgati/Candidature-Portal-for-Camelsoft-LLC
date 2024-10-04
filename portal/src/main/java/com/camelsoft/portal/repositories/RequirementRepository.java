package com.camelsoft.portal.repositories;

import com.camelsoft.portal.models.Requirement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequirementRepository extends JpaRepository<Requirement, Integer> {
}
