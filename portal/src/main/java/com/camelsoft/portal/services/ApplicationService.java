package com.camelsoft.portal.services;


import com.camelsoft.portal.common.ApplicationSpecification;
import com.camelsoft.portal.common.PageResponse;
import com.camelsoft.portal.customExceptions.ApplicationNotFoundException;
import com.camelsoft.portal.customExceptions.JobNotFoundException;
import com.camelsoft.portal.dtos.*;
import com.camelsoft.portal.files.FileStorageService;
import com.camelsoft.portal.models.*;
import com.camelsoft.portal.repositories.ApplicationRepository;
import com.camelsoft.portal.repositories.JobRepository;
import com.camelsoft.portal.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService implements UserService {


    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final FileStorageService fileStorageService;
    private final EmailService emailService;
    private final JobService jobService;

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

    public void uploadCv(MultipartFile file) {
        User currentUser = getCurrentUser();
        String cvPath = fileStorageService.saveFile(file, currentUser.getId(), "cv");

        Application application = new Application();
        application.setCreatedDate(new Date());
        application.setCv(cvPath);
        application.setType(ApplicationType.UNSOLICITED);
        application.setStatus(ApplicationStatus.SUBMITTED);
        application.setUser(currentUser);
        application.setArchived(false);

        applicationRepository.save(application);
    }
    public void applyForJob(MultipartFile file, Integer jobId) {
        User currentUser = getCurrentUser();
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + jobId));

        String cvPath = fileStorageService.saveFile(file, currentUser.getId(),"cv");

        Application application = new Application();
        application.setCreatedDate(new Date());
        application.setUser(currentUser);
        application.setJob(job);
        application.setCv(cvPath);
        application.setType(ApplicationType.NORMAL);
        application.setStatus(ApplicationStatus.SUBMITTED);
        application.setArchived(false);


        applicationRepository.save(application);
        sendApplicationConfirmationEmail(application);
    }
    private void sendApplicationConfirmationEmail(Application application) {
        String userEmail = application.getUser().getEmail();
        String subject = "Your application has been submitted";
        String text = String.format("Dear %s,\n\nThank you for applying to the job '%s'. Your application has been successfully submitted.\n\nBest regards,\nCamelSoft",
                application.getUser().fullName(),
                application.getJob().getTitle());

        MailBody mailBody = MailBody.builder()
                .to(userEmail)
                .subject(subject)
                .text(text)
                .build();

        emailService.sendSimpleMessage(mailBody);
    }
    @Transactional
    public void archiveApplication(Integer applicationId, boolean archived) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found with id: " + applicationId));

        application.setArchived(archived);
        applicationRepository.save(application);
    }

    public PageResponse<ApplicationResponse> findArchivedApplicationsByCategory(String categoryTitle,int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Application> applications = applicationRepository.findArchivedApplicationsByCategory(categoryTitle,pageable);

        List<ApplicationResponse> applicationResponses = applications.stream()
                .map(this::mapToApplicationResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(
                applicationResponses,
                applications.getNumber(),
                applications.getSize(),
                applications.getTotalElements(),
                applications.getTotalPages(),
                applications.isFirst(),
                applications.isLast()
        );
    }
    public PageResponse<ApplicationResponse> findUnsolicitedApplications(int page, int size) {
        Pageable pageable = PageRequest.of(page, size,Sort.by("createdDate").descending());

        Page<Application> applications = applicationRepository.findByType(ApplicationType.UNSOLICITED, pageable);

        List<ApplicationResponse> applicationResponses = applications.stream()
                .map(this::mapToApplicationResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(
                applicationResponses,
                applications.getNumber(),
                applications.getSize(),
                applications.getTotalElements(),
                applications.getTotalPages(),
                applications.isFirst(),
                applications.isLast()
        );
    }
    public PageResponse<ApplicationResponse> filterApplications(
            String jobTitle,
            ApplicationStatus status,
            Date startDate,
            Date endDate,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size,Sort.by("createdDate").descending());
        Specification<Application> spec = Specification.where(ApplicationSpecification.hasJobTitle(jobTitle))
                .and(ApplicationSpecification.hasStatus(status))
                .and(ApplicationSpecification.isCreatedAfter(startDate))
                .and(ApplicationSpecification.isCreatedBefore(endDate));

        Page<Application> applications = applicationRepository.findAll(spec, pageable);

        List<ApplicationResponse> applicationResponses = applications.stream()
                .map(this::mapToApplicationResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(
                applicationResponses,
                applications.getNumber(),
                applications.getSize(),
                applications.getTotalElements(),
                applications.getTotalPages(),
                applications.isFirst(),
                applications.isLast()
        );
    }

    @Transactional
    public void updateApplicationStatus(Integer applicationId, ApplicationStatus newStatus) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found with id: " + applicationId));

        application.setStatus(newStatus);
        applicationRepository.save(application);
        sendStatusUpdateEmail(application);
    }

    private void sendStatusUpdateEmail(Application application) {
        String userEmail = application.getUser().getEmail();
        String subject = "Your application status has been updated";
        String text = String.format("Dear %s,\n\nYour application for the job '%s' you have been: %s.\n\nBest regards,\nCamelSoft",
                application.getUser().fullName(),
                application.getJob().getTitle(),
                application.getStatus().name());

        MailBody mailBody = MailBody.builder()
                .to(userEmail)
                .subject(subject)
                .text(text)
                .build();

        emailService.sendSimpleMessage(mailBody);
    }

    public PageResponse<JobResponse> findJobsByUser(Integer userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Application> applications = applicationRepository.findByUser_Id(userId, pageable);

        List<JobResponse> jobResponses = applications.stream()
                .map(Application::getJob) // Extract the job first
                .filter(Objects::nonNull) // Filter out null jobs
                .map(jobService::mapToJobResponse) // Map non-null jobs to JobResponse
                .collect(Collectors.toList());

        return new PageResponse<>(
                jobResponses,
                applications.getNumber(),
                applications.getSize(),
                applications.getTotalElements(),
                applications.getTotalPages(),
                applications.isFirst(),
                applications.isLast()
        );
    }



    public byte[] downloadCv(Integer applicationId) throws IOException {
        String cvPath = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found with id: " + applicationId))
                .getCv();

        Path path = new File(cvPath).toPath();
        return Files.readAllBytes(path);
    }

    public void deleteApplication(Integer applicationId) {
        if (!applicationRepository.existsById(applicationId)) {
            throw new JobNotFoundException("No application found with id: " + applicationId);
        }
        applicationRepository.deleteById(applicationId);
    }

    public DashboardSummaryResponse getAdminDashboardSummary() {
        DashboardSummaryResponse summary = new DashboardSummaryResponse();

        // Get the number of active jobs (status = OPEN)
        summary.setActiveJobListings(jobRepository.countByStatus(JobStatus.OPEN));

        // Get the total number of applications
        summary.setTotalApplications(applicationRepository.count());

        // Get the number of applications with status UNDER_REVIEW
        summary.setPendingReviews(applicationRepository.countByStatus(ApplicationStatus.UNDER_REVIEW));

        return summary;
    }


    private ApplicationResponse mapToApplicationResponse(Application application) {
        ApplicationResponse applicationResponse = new ApplicationResponse();
        applicationResponse.setId(application.getId());
        applicationResponse.setCreatedDate(application.getCreatedDate());
        applicationResponse.setCvPath(application.getCv());
        applicationResponse.setArchived(application.isArchived());
        applicationResponse.setType(application.getType().name());
        applicationResponse.setStatus(application.getStatus().name());
        applicationResponse.setJobTitle(application.getJob()!=null?application.getJob().getTitle():null);
        applicationResponse.setUserName(application.getUser().fullName());
        return applicationResponse;
    }
}


