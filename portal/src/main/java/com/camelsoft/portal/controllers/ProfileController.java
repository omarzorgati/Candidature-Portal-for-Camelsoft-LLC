package com.camelsoft.portal.controllers;



import com.camelsoft.portal.dtos.*;
import com.camelsoft.portal.models.Certification;
import com.camelsoft.portal.models.EducationalDegree;
import com.camelsoft.portal.models.Skill;
import com.camelsoft.portal.models.WorkExperience;
import com.camelsoft.portal.services.ProfileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("profile")
@RequiredArgsConstructor
@Tag(name= "profileManagement")
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/add_skill")
    public ResponseEntity<Skill> addSkill(@RequestParam String skill) {
        Skill newSkill = profileService.createNewSkill(skill);
        return new ResponseEntity<>(newSkill, HttpStatus.CREATED);
    }
    @PostMapping("/add_educational_degree")
    public ResponseEntity<EducationalDegree> addEducationalDegree(
            @RequestBody @Valid EducationalDegreeRequest educationalDegreeRequest) {
        EducationalDegree newEducationalDegree = profileService.createNewEducationalDegree(educationalDegreeRequest);
        return new ResponseEntity<>(newEducationalDegree, HttpStatus.CREATED);
    }
    @PostMapping("/add_certification")
    public ResponseEntity<Certification> addCertification(@RequestBody @Valid CertificationRequest certificationRequest) {
        Certification newCertification = profileService.createNewCertification(certificationRequest);
        return new ResponseEntity<>(newCertification, HttpStatus.CREATED);
    }
    @PostMapping("/add_work_experience")
    public ResponseEntity<WorkExperience> addWorkExperience(@RequestBody @Valid WorkExperienceRequest workExperienceRequest) {
        WorkExperience newWorkExperience = profileService.createNewWorkExperience(workExperienceRequest);
        return new ResponseEntity<>(newWorkExperience, HttpStatus.CREATED);
    }
    @PostMapping("/complete_profile")
    public ResponseEntity<String> completeProfile(@RequestBody @Valid ProfileRequest profileRequest) {
        profileService.completeProfile(profileRequest);
        return new ResponseEntity<>("Profile completed successfully", HttpStatus.OK);
    }
    @PostMapping("/add-link")
    public ResponseEntity<String> addLink(@RequestParam String link) {
        profileService.addLink(link);
        return new ResponseEntity<>("Link added successfully", HttpStatus.OK);
    }
    @PostMapping(value = "/avatar", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadUserAvatar(@RequestPart("file") MultipartFile file) {
        profileService.uploadUserAvatar(file);
        return ResponseEntity.accepted().build();
    }
}
