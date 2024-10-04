package com.camelsoft.portal.controllers;


import com.camelsoft.portal.common.PageResponse;
import com.camelsoft.portal.dtos.ApplicationResponse;
import com.camelsoft.portal.dtos.DashboardSummaryResponse;
import com.camelsoft.portal.dtos.JobResponse;
import com.camelsoft.portal.models.Application;
import com.camelsoft.portal.models.ApplicationStatus;
import com.camelsoft.portal.services.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;


@RestController
@RequestMapping("/application")
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService applicationService;
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/upload-cv", consumes = "multipart/form-data")
    public ResponseEntity<Application> uploadCv(
            @RequestPart("file") MultipartFile file
    ) {
        applicationService.uploadCv(file);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PostMapping(value = "/apply/{jobId}", consumes = "multipart/form-data")
    public ResponseEntity<Void> applyForJob(
            @PathVariable("jobId") Integer jobId,
            @RequestPart("file") MultipartFile file
    ) {
        applicationService.applyForJob(file, jobId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/archive/{application-id}/archived")
    public ResponseEntity<Void> archiveApplication(
            @PathVariable("application-id") Integer applicationId,
            @RequestParam("archived") boolean archived) {
        applicationService.archiveApplication(applicationId, archived);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("update-status/{application-id}/status")
    public ResponseEntity<Void> updateApplicationStatus(
            @PathVariable("application-id") Integer applicationId,
            @RequestParam("status") ApplicationStatus status
    ) {
        applicationService.updateApplicationStatus(applicationId, status);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/archived-by-category")
    public ResponseEntity<PageResponse<ApplicationResponse>> findArchivedApplicationsByCategory(
            @RequestParam(value = "category",required = false) String categoryTitle,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(applicationService.findArchivedApplicationsByCategory(categoryTitle, page, size));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/unsolicited")
    public ResponseEntity<PageResponse<ApplicationResponse>> findUnsolicitedApplications(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(applicationService.findUnsolicitedApplications(page, size));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/filter")
    public ResponseEntity<PageResponse<ApplicationResponse>> filterApplications(
            @RequestParam(name = "jobTitle") String jobTitle,
            @RequestParam(name = "status", required = false) ApplicationStatus status,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(applicationService.filterApplications(jobTitle, status, startDate, endDate, page, size));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/candidate-jobs/{user-id}")
    public ResponseEntity<PageResponse<JobResponse>> findJobsByUser(
            @PathVariable("user-id") Integer userId,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(applicationService.findJobsByUser(userId, page, size));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/download-cv/{application-id}")
    public ResponseEntity<ByteArrayResource> downloadCv(@PathVariable("application-id") Integer applicationId) throws IOException {
        byte[] data = applicationService.downloadCv(applicationId);

        ByteArrayResource resource = new ByteArrayResource(data);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=cv_" + applicationId + ".pdf")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(data.length)
                .body(resource);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{application-id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable("application-id") Integer applicationId) {
        applicationService.deleteApplication(applicationId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/dashboard-summary")
    public ResponseEntity<DashboardSummaryResponse> getDashboardSummary() {
        DashboardSummaryResponse summary = applicationService.getAdminDashboardSummary();
        return ResponseEntity.ok(summary);
    }
}
