package com.camelsoft.portal.common;

import com.camelsoft.portal.models.Application;
import com.camelsoft.portal.models.ApplicationStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

public class ApplicationSpecification {

    public static Specification<Application> hasJobTitle(String jobTitle) {
        return (root, query, criteriaBuilder) ->
                jobTitle == null || jobTitle.trim().isEmpty() ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("job").get("title"), jobTitle);
    }

    public static Specification<Application> hasStatus(ApplicationStatus status) {
        return (root, query, criteriaBuilder) ->
                status == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Application> isCreatedAfter(Date startDate) {
        return (root, query, criteriaBuilder) ->
                startDate == null ? criteriaBuilder.conjunction() : criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"), startDate);
    }

    public static Specification<Application> isCreatedBefore(Date endDate) {
        return (root, query, criteriaBuilder) ->
                endDate == null ? criteriaBuilder.conjunction() : criteriaBuilder.lessThanOrEqualTo(root.get("createdDate"), endDate);
    }
}
