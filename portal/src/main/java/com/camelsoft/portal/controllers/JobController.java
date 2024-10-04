package com.camelsoft.portal.controllers;


import com.camelsoft.portal.common.PageResponse;
import com.camelsoft.portal.dtos.CategoryRequest;
import com.camelsoft.portal.dtos.JobRequest;
import com.camelsoft.portal.dtos.JobResponse;
import com.camelsoft.portal.dtos.RequirementRequest;
import com.camelsoft.portal.models.Category;
import com.camelsoft.portal.models.JobStatus;
import com.camelsoft.portal.models.Requirement;
import com.camelsoft.portal.services.JobService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("job")
@RequiredArgsConstructor
@Tag(name= "job")


public class JobController {

    private final JobService jobService;


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<Void> createJob(@RequestBody @Valid JobRequest jobRequest) {
        jobService.createJob(jobRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/requirement/{job-id}")
    public ResponseEntity<Requirement> addRequirement(
            @PathVariable ("job-id") Integer jobId,
            @RequestBody @Valid RequirementRequest requirementRequest
    ) {
        Requirement requirement = jobService.addRequirement(requirementRequest,jobId);
        return new ResponseEntity<>(requirement, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/category/{job-id}")
    public ResponseEntity<Category> addCategory(
            @PathVariable ("job-id") Integer jobId,
            @RequestBody @Valid CategoryRequest categoryRequest
    ) {
        Category category = jobService.addCategory(categoryRequest,jobId);
        return new ResponseEntity<>(category, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{jobId}/status")
    public ResponseEntity<Void> updateJobStatus(
            @PathVariable("jobId") Integer jobId,
            @RequestParam("status") JobStatus status
    ) {
        jobService.updateJobStatus(jobId, status);
        return ResponseEntity.status(HttpStatus.OK).build();    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{jobId}")
    public ResponseEntity<Void> updateJob(
            @PathVariable("jobId") Integer jobId,
            @RequestBody @Valid JobRequest jobRequest
    ) {
        jobService.updateJob(jobId, jobRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update_requirement/{id}")
    public ResponseEntity<Requirement> updateRequirement(
            @PathVariable("id") Integer requirementId,
            @RequestBody @Valid RequirementRequest requirementRequest) {

        Requirement updatedRequirement = jobService.updateRequirement(requirementId, requirementRequest);
        return ResponseEntity.ok(updatedRequirement);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update_category/{id}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable("id") Integer categoryId,
            @RequestBody @Valid CategoryRequest categoryRequest) {

        Category updatedCategory = jobService.updateCategory(categoryId, categoryRequest);
        return ResponseEntity.ok(updatedCategory);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("delete/{job-id}")
    public ResponseEntity<?> deleteJob(@PathVariable("job-id") Integer jobId) {
        jobService.deleteJobById(jobId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search-all")
    public ResponseEntity<PageResponse<JobResponse>> searchAllJobs(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(jobService.searchAllJobs(page, size));
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<JobResponse>> searchJobs(
            @RequestParam String experienceLevel,
            @RequestParam String jobType,
            @RequestParam String category,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(jobService.searchJobs(experienceLevel, jobType, category, page, size));
    }


    @GetMapping("/search-by-id/{job-id}")
    public ResponseEntity<JobResponse> getJobById(@PathVariable("job-id") Integer jobId) {
        JobResponse jobResponse = jobService.findJobById(jobId);
        return ResponseEntity.ok(jobResponse);
    }


}
