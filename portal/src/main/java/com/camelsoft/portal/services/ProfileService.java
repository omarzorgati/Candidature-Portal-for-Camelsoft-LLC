package com.camelsoft.portal.services;


import com.camelsoft.portal.dtos.*;
import com.camelsoft.portal.files.FileStorageService;
import com.camelsoft.portal.models.*;
import com.camelsoft.portal.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;


@Service
@RequiredArgsConstructor
public class ProfileService implements UserService {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final EducationalDegreeRepository educationalDegreeRepository;
    private final CertificationRepository certificationRepository;
    private final WorkExperienceRepository workExperienceRepository;
    private final FileStorageService fileStorageService;

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

    public Skill createNewSkill(String skillName) {
        User currentUser = getCurrentUser();

        Skill newSkill = new Skill();
        newSkill.setSkill(skillName);
        newSkill.setCreatedDate(new Date());
        newSkill.setUser(currentUser);

        return skillRepository.save(newSkill);
    }

    public EducationalDegree createNewEducationalDegree(EducationalDegreeRequest educationalDegreeRequest) {
        User currentUser = getCurrentUser();

        EducationalDegree educationalDegree = new EducationalDegree();
        educationalDegree.setDegreeTitle(educationalDegreeRequest.getDegreeTitle());
        educationalDegree.setDegreeDescription(educationalDegreeRequest.getDegreeDescription());
        educationalDegree.setDate(educationalDegreeRequest.getDate());
        educationalDegree.setCreatedDate(new Date());
        educationalDegree.setUser(currentUser);

        return educationalDegreeRepository.save(educationalDegree);
    }

    public Certification createNewCertification(CertificationRequest certificationRequest) {
        User currentUser = getCurrentUser();

        Certification certification = new Certification();
        certification.setTitle(certificationRequest.getTitle());
        certification.setDescription(certificationRequest.getDescription());
        certification.setDate(certificationRequest.getDate());
        certification.setCreatedDate(new Date());
        certification.setUser(currentUser);

        return certificationRepository.save(certification);
    }

    public WorkExperience createNewWorkExperience(WorkExperienceRequest workExperienceRequest) {
        User currentUser = getCurrentUser();

        WorkExperience workExperience = new WorkExperience();
        workExperience.setTitle(workExperienceRequest.getTitle());
        workExperience.setDescription(workExperienceRequest.getDescription());
        workExperience.setDate(workExperienceRequest.getDate());
        workExperience.setCreatedDate(new Date());
        workExperience.setUser(currentUser);

        return workExperienceRepository.save(workExperience);
    }

    public void completeProfile(ProfileRequest profileRequest) {
        User user = getCurrentUser();

        user.setFirstname(profileRequest.getFirstname());
        user.setLastname(profileRequest.getLastname());
        user.setEmail(profileRequest.getEmail());
        user.setPhoneNumber(profileRequest.getPhoneNumber());
        user.setCity(profileRequest.getCity());
        user.setLastPost(profileRequest.getLastPost());
        user.setDegreeLevel(profileRequest.getDegreeLevel());
        user.setEnglishLevel(profileRequest.getEnglishLevel());
        user.setContractType(profileRequest.getContractType());

        userRepository.save(user);
    }

    public void addLink(String link) {
        User user = getCurrentUser();
        user.setLink(link);
        userRepository.save(user);
    }

    public void uploadUserAvatar(MultipartFile file) {
        User currentUser = getCurrentUser();
        String avatarPath = fileStorageService.saveFile(file, currentUser.getId(),"avatar");
        currentUser.setAvatar(avatarPath);
        userRepository.save(currentUser);

    }
}
