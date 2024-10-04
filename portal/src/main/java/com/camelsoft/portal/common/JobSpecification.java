package com.camelsoft.portal.common;

import com.camelsoft.portal.models.Job;
import org.springframework.data.jpa.domain.Specification;

public class JobSpecification {

    public static Specification<Job> hasExperienceLevel(String experienceLevel) {
        return (root, query, criteriaBuilder) -> {
            if (experienceLevel == null || experienceLevel.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("experienceLevel"), experienceLevel);
        };
    }

    public static Specification<Job> hasJobType(String jobType) {
        return (root, query, criteriaBuilder) -> {
            if (jobType == null || jobType.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("jobType"), jobType);
        };
    }

    public static Specification<Job> hasCategoryTitle(String categoryTitle) {
        return (root, query, criteriaBuilder) -> {
            if (categoryTitle == null || categoryTitle.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.join("categories").get("title"), categoryTitle);
        };
    }
}