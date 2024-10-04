package com.camelsoft.portal.services;


import com.camelsoft.portal.common.JobSpecification;
import com.camelsoft.portal.common.PageResponse;
import com.camelsoft.portal.customExceptions.CategoryNotFoundException;
import com.camelsoft.portal.customExceptions.JobNotFoundException;
import com.camelsoft.portal.customExceptions.RequirementNotFoundException;
import com.camelsoft.portal.dtos.CategoryRequest;
import com.camelsoft.portal.dtos.JobRequest;
import com.camelsoft.portal.dtos.JobResponse;
import com.camelsoft.portal.dtos.RequirementRequest;
import com.camelsoft.portal.models.*;
import com.camelsoft.portal.repositories.CategoryRepository;
import com.camelsoft.portal.repositories.JobRepository;
import com.camelsoft.portal.repositories.RequirementRepository;
import com.camelsoft.portal.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobService implements UserService{

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final RequirementRepository requirementRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No user is currently authenticated");
        }
        User user = (User) authentication.getPrincipal();
        return userRepository.findById(user.getId()).orElseThrow(() ->
                new IllegalStateException("User not found"));
    }

    public void createJob(JobRequest jobRequest) {
        Job job = new Job();
        job.setTitle(jobRequest.getTitle());
        job.setDescription(jobRequest.getDescription());
        job.setResponsibilities(jobRequest.getResponsibilities());
        job.setExperienceLevel(jobRequest.getExperienceLevel());
        job.setJobType(jobRequest.getJobType());
        job.setLocation(jobRequest.getLocation());
        job.setPostDate(jobRequest.getPostDate());
        job.setCloseDate(jobRequest.getCloseDate());
        job.setStatus(jobRequest.getStatus());
        job.setCreatedDate(new Date());

        User currentUser = getCurrentUser();
        job.getUsers().add(currentUser);

        jobRepository.save(job);
    }
    public Requirement addRequirement(RequirementRequest requirementRequest,Integer jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job not found"+ jobId));

        Requirement requirement = new Requirement();
        requirement.setDescription(requirementRequest.getDescription());
        requirement.setCreatedDate(new Date());
        requirement.setJob(job);


        return requirementRepository.save(requirement);
    }

    public Category addCategory(CategoryRequest categoryRequest, Integer jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job not found"+ jobId));

        Category category = new Category();
        category.setTitle(categoryRequest.getTitle());
        category.setCreatedDate(new Date());
        category.setJob(job);

        return categoryRepository.save(category);
    }

    public void updateJobStatus(Integer jobId, JobStatus status) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + jobId));
        job.setStatus(status);
        jobRepository.save(job);
    }
    public void updateJob(Integer jobId, JobRequest jobRequest) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + jobId));

        job.setTitle(jobRequest.getTitle());
        job.setDescription(jobRequest.getDescription());
        job.setResponsibilities(jobRequest.getResponsibilities());
        job.setExperienceLevel(jobRequest.getExperienceLevel());
        job.setJobType(jobRequest.getJobType());
        job.setLocation(jobRequest.getLocation());
        job.setPostDate(jobRequest.getPostDate());
        job.setCloseDate(jobRequest.getCloseDate());
        job.setStatus(jobRequest.getStatus());

       jobRepository.save(job);
    }

    public Requirement updateRequirement(Integer requirementId, RequirementRequest requirementRequest) {
        Requirement requirement = requirementRepository.findById(requirementId)
                .orElseThrow(() -> new RequirementNotFoundException("Requirement not found with id:" + requirementId ));

        requirement.setDescription(requirementRequest.getDescription());

        return requirementRepository.save(requirement);
    }

    public Category updateCategory(Integer categoryId, CategoryRequest categoryRequest) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id:" + categoryId));

        category.setTitle(categoryRequest.getTitle());
        return categoryRepository.save(category);
    }
    public void deleteJobById(Integer jobId) {
        if (!jobRepository.existsById(jobId)) {
            throw new JobNotFoundException("No job found with id: " + jobId);
        }
        jobRepository.deleteById(jobId);
    }

    public PageResponse<JobResponse> searchAllJobs(
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Job> jobs = jobRepository.findAll(pageable);

        List<JobResponse> jobResponses = jobs.stream()
                .map(this::mapToJobResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(
                jobResponses,
                jobs.getNumber(),
                jobs.getSize(),
                jobs.getTotalElements(),
                jobs.getTotalPages(),
                jobs.isFirst(),
                jobs.isLast()
        );
    }


    public PageResponse<JobResponse> searchJobs(
            String experienceLevel,
            String jobType,
            String categoryTitle,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Specification<Job> spec = Specification.where(JobSpecification.hasExperienceLevel(experienceLevel))
                .and(JobSpecification.hasJobType(jobType))
                .and(JobSpecification.hasCategoryTitle(categoryTitle));

        Page<Job> jobs = jobRepository.findAll(spec, pageable);

        List<JobResponse> jobResponses = jobs.stream()
                .map(this::mapToJobResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(
                jobResponses,
                jobs.getNumber(),
                jobs.getSize(),
                jobs.getTotalElements(),
                jobs.getTotalPages(),
                jobs.isFirst(),
                jobs.isLast()
        );
    }

    public JobResponse findJobById(Integer jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + jobId));

        User currentUser = getCurrentUser();

        if (job.getViewers() == null) {
            job.setViewers(new HashSet<>());
        }

        if (!job.getViewers().contains(currentUser)) {
            job.setViewCount(job.getViewCount() == null ? 1 : job.getViewCount() + 1);
            job.getViewers().add(currentUser);
            jobRepository.save(job);
        }

        return mapToJobResponse(job);
    }

    public JobResponse mapToJobResponse(Job job) {
        JobResponse jobResponse = new JobResponse();
        jobResponse.setId(job.getId());
        jobResponse.setTitle(job.getTitle());
        jobResponse.setDescription(job.getDescription());
        jobResponse.setResponsibilities(job.getResponsibilities());
        jobResponse.setExperienceLevel(job.getExperienceLevel());
        jobResponse.setJobType(job.getJobType());
        jobResponse.setLocation(job.getLocation());
        jobResponse.setCreatedDate(job.getCreatedDate());
        jobResponse.setPostDate(job.getPostDate());
        jobResponse.setCloseDate(job.getCloseDate());
        jobResponse.setStatus(job.getStatus().name());

        // Extract category titles
        jobResponse.setCategories(
                job.getCategories().stream()
                        .map(Category::getTitle)
                        .collect(Collectors.toSet())
        );
        jobResponse.setViewCount(job.getViewCount());

        return jobResponse;
    }


}
