package com.camelsoft.portal.repositories;

import com.camelsoft.portal.models.Certification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificationRepository extends JpaRepository<Certification, Integer> {
}
