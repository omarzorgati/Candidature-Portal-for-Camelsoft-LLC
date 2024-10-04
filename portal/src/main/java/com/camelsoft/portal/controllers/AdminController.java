package com.camelsoft.portal.controllers;

import com.camelsoft.portal.common.PageResponse;
import com.camelsoft.portal.dtos.UserResponse;
import com.camelsoft.portal.services.AdminService;
import com.camelsoft.portal.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AuthenticationService authenticationService;
    private final AdminService adminService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/elevate")
    public ResponseEntity<?> elevateUserToAdmin(@RequestParam String email) {
        authenticationService.elevateToAdmin(email);
        return ResponseEntity.ok("User elevated to admin");
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all-users")
    public ResponseEntity<PageResponse<UserResponse>> findAllUsers(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(adminService.findAllUsers(page, size));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("{user-id}")
    public ResponseEntity<UserResponse> findUserById(@PathVariable("user-id") Integer userId){
        return ResponseEntity.ok(adminService.findById(userId));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("delete-user/{user-id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("user-id") Integer userId) {
        adminService.deleteUserById(userId);
        return ResponseEntity.noContent().build();
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get-user-by-skill/{skill}")
    public ResponseEntity<PageResponse<UserResponse>> findUsersBySkill(
            @PathVariable("skill") String skill,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(adminService.findUsersBySkill(skill,page,size));
    }

}
