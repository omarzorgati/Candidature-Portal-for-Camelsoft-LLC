package com.camelsoft.portal.services;

import com.camelsoft.portal.customExceptions.PasswordsDoNotMatchException;
import com.camelsoft.portal.dtos.*;
import com.camelsoft.portal.models.Role;
import com.camelsoft.portal.models.RoleName;
import com.camelsoft.portal.models.Token;
import com.camelsoft.portal.models.User;
import com.camelsoft.portal.repositories.RoleRepository;
import com.camelsoft.portal.repositories.TokenRepository;
import com.camelsoft.portal.repositories.UserRepository;
import com.camelsoft.portal.security.JwtService;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.HashMap;


@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;

    @PostConstruct
    public void init() {
        if (roleRepository.findByName(RoleName.USER).isEmpty()) {
            roleRepository.save(new Role(null, RoleName.USER, null));
        }

        if (roleRepository.findByName(RoleName.ADMIN).isEmpty()) {
            roleRepository.save(new Role(null, RoleName.ADMIN, null));
        }
    }

    public void register(RegistrationRequest request) throws MessagingException {
        if (!request.getPassword().equals(request.getRepeatPassword())) {
            throw new PasswordsDoNotMatchException("Passwords do not match");
        }

        var userRole = roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new IllegalStateException("User role not found"));

        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(true)
                .role(userRole)
                .build();

        userRepository.save(user);

    }

    public void elevateToAdmin(String email) {
        Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                .orElseThrow(() -> new IllegalStateException("Admin role not found"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setRole(adminRole);
        userRepository.save(user);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var claims = new HashMap<String, Object>();
        var user = ((User) auth.getPrincipal());
        // Update user with deviceId and tokenId
        user.setDeviceId(request.getDeviceId());
        user.setTokenId(request.getTokenId());
        userRepository.save(user);
        claims.put("fullName", user.fullName());
        var jwtToken = jwtService.generateToken(claims,user);
        //we need to revoke all tokens before getting a new one
        revokeAllUserTokens(user);
        saveUserToken(user,jwtToken);
        return AuthenticationResponse.builder()
                .token(jwtToken).build();
    }

    private void saveUserToken(User user,String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(token);
    }
    private void revokeAllUserTokens(User user){
        var validUserTokens = tokenRepository.findAllValidTokensByUserId(user.getId());
        if(validUserTokens.isEmpty()){
            return;
        }
        validUserTokens.forEach(t -> {
            t.setExpired(true);
            t.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

}


