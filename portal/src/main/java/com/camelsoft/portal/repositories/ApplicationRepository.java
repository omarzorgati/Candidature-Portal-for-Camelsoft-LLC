package com.camelsoft.portal.repositories;

import com.camelsoft.portal.models.Application;
import com.camelsoft.portal.models.ApplicationStatus;
import com.camelsoft.portal.models.ApplicationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ApplicationRepository extends JpaRepository<Application, Integer>, JpaSpecificationExecutor<Application> {
    @Query("SELECT a FROM Application a LEFT JOIN a.job j LEFT JOIN j.categories c " +
            "WHERE a.archived = true " +
            "AND (:categoryTitle IS NULL OR :categoryTitle = '' OR c.title = :categoryTitle)")
    Page<Application> findArchivedApplicationsByCategory(@Param("categoryTitle") String categoryTitle, Pageable pageable);

    Page<Application> findByType(ApplicationType type, Pageable pageable);

    Page<Application> findByUser_Id(Integer userId, Pageable pageable);

    long countByStatus(ApplicationStatus status);
}
