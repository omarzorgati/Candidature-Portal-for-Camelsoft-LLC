package com.camelsoft.portal.services;


import com.camelsoft.portal.common.PageResponse;
import com.camelsoft.portal.dtos.UserResponse;
import com.camelsoft.portal.models.Skill;
import com.camelsoft.portal.models.User;
import com.camelsoft.portal.repositories.SkillRepository;
import com.camelsoft.portal.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;



    public PageResponse<UserResponse> findAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<User> users = userRepository.findAll(pageable);

        List<UserResponse> userResponses = users.stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(
                userResponses,
                users.getNumber(),
                users.getSize(),
                users.getTotalElements(),
                users.getTotalPages(),
                users.isFirst(),
                users.isLast()
        );
    }
    public UserResponse findById(Integer userId) {
        return userRepository.findById(userId)
                .map(this::mapToUserResponse)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id " + userId));
    }

    public void deleteUserById(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new UsernameNotFoundException("User not found with id " + userId);
        }
        userRepository.deleteById(userId);
    }
    public PageResponse<UserResponse> findUsersBySkill(String skill,int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Skill> skills = skillRepository.findBySkill(skill,pageable);
        List<UserResponse> userResponses = skills.stream()
                .map(Skill::getUser)
                .distinct()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(
                userResponses,
                skills.getNumber(),
                skills.getSize(),
                skills.getTotalElements(),
                skills.getTotalPages(),
                skills.isFirst(),
                skills.isLast()
        );
    }


    private UserResponse mapToUserResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setFirstname(user.getFirstname());
        userResponse.setLastname(user.getLastname());
        userResponse.setEmail(user.getEmail());
        userResponse.setPhoneNumber(user.getPhoneNumber());
        userResponse.setCity(user.getCity());
        userResponse.setLink(user.getLink());
        userResponse.setLastPost(user.getLastPost());
        userResponse.setDegreeLevel(user.getDegreeLevel());
        userResponse.setEnglishLevel(user.getEnglishLevel());
        userResponse.setContractType(user.getContractType());
        userResponse.setAccountLocked(user.getAccountLocked());
        userResponse.setEnabled(user.getEnabled());
        userResponse.setRole(user.getRole().getName().name());
        userResponse.setAvatar(user.getAvatar());
        userResponse.setCreatedDate(user.getCreatedDate());
        return userResponse;
    }


}


