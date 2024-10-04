package com.camelsoft.portal.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardSummaryResponse {
    private long activeJobListings;
    private long totalApplications;
    private long pendingReviews;
}
