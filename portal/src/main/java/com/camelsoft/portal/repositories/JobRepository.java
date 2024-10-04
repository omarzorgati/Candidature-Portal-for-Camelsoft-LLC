package com.camelsoft.portal.repositories;

import com.camelsoft.portal.models.Job;
import com.camelsoft.portal.models.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface JobRepository extends JpaRepository<Job, Integer>, JpaSpecificationExecutor<Job> {

    long countByStatus(JobStatus status);
}
