package com.camelsoft.portal.controllers;


import com.camelsoft.portal.customExceptions.InvalidOtpException;
import com.camelsoft.portal.customExceptions.OtpExpiredException;
import com.camelsoft.portal.customExceptions.PasswordsDoNotMatchException;
import com.camelsoft.portal.dtos.ChangePasswordRequest;
import com.camelsoft.portal.dtos.MailBody;
import com.camelsoft.portal.models.ForgotPassword;
import com.camelsoft.portal.models.User;
import com.camelsoft.portal.repositories.ForgotPasswordRepository;
import com.camelsoft.portal.repositories.UserRepository;
import com.camelsoft.portal.services.EmailService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
//from open api
@Tag(name = "ForgotPassword")
public class ForgotPasswordController {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ForgotPasswordRepository forgotPasswordRepository;
    private final PasswordEncoder passwordEncoder;


    //send mail for email verification
    @PostMapping("/verifyMail/{email}")
    public ResponseEntity<String> verifyMail(
            @PathVariable("email") String email
    ) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("please provide valid email!"));
        int otp = otpGenerator();
        MailBody mailBody = MailBody.builder()
                .to(email)
                .text("this is the OTP for your forgot password" + otp)
                .subject("OTP forgot password")
                .build();
        Optional<ForgotPassword> existingFp = forgotPasswordRepository.findByUser(user);
        ForgotPassword fp;
        if (existingFp.isPresent()) {
            fp = existingFp.get();
            fp.setOtp(otp);
            fp.setExpirationTime(new Date(System.currentTimeMillis() + 600 * 1000)); // 10 minutes
        } else {
            fp = ForgotPassword.builder()
                    .otp(otp)
                    .expirationTime(new Date(System.currentTimeMillis() + 600 * 1000))
                    .user(user)
                    .build();
        }
        emailService.sendSimpleMessage(mailBody);
        forgotPasswordRepository.save(fp);
        return ResponseEntity.ok("Email sent for verification");
    }


    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<String> verifyOtp(
            @PathVariable("otp") Integer otp,
            @PathVariable("email") String email
    ){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("please provide valid email!"));
        ForgotPassword fp = forgotPasswordRepository.findByOtpAndUser(otp,user)
                .orElseThrow(() -> new InvalidOtpException("Invalid otp for email"+ email));
        if(fp.getExpirationTime().before(Date.from(Instant.now()))){
            forgotPasswordRepository.deleteById(fp.getId());
            throw new OtpExpiredException ("OTP has expired");
        }
        return ResponseEntity.ok("OTP verified");

    }

    @PostMapping("/changePassword/{email}")
    public ResponseEntity<String> changePasswordHandler(
            @RequestBody @Valid ChangePasswordRequest changePasswordRequest,
            @PathVariable("email") String email
    ){
        if(!Objects.equals(changePasswordRequest.password(), changePasswordRequest.repeatPassword())){
            throw new PasswordsDoNotMatchException("Passwords do not match");
        }
        String encodedPassword = passwordEncoder.encode(changePasswordRequest.password());
        userRepository.updatePassword(email,encodedPassword);
        return ResponseEntity.ok("Password changed");

    }



    private Integer otpGenerator(){
        Random random = new Random();
        return random.nextInt(100_000,9999_999);

    }

}
