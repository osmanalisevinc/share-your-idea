package com.share.controller.authcontroller;


import com.share.constants.Constants;
import com.share.controller.authcontroller.request.*;
import com.share.controller.authcontroller.response.JwtResponse;
import com.share.model.User;
import com.share.model.dto.MessageResponse;
import com.share.repository.UserRepository;
import com.share.security.services.AuthService;
import com.share.service.UserTransactionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserTransactionService userTransactionService;
    private final UserRepository userRepository;

    @PostMapping("/signup")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

        authService.register(signUpRequest, null);

        return ResponseEntity.ok(Constants.USER_SUCCESSFULLY_SAVED);
    }

    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        JwtResponse jwtResponse = authService.signIn(loginRequest);
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(EntityNotFoundException::new);
        jwtResponse.setUsername(user.getUserName());
        jwtResponse.setProfilePhoto(user.getProfilePhoto());
        userTransactionService.login();
        return ResponseEntity.ok(jwtResponse);
    }

    @PutMapping("/update-password")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Object> updatePassword(@Valid @RequestBody UpdatePasswordRequest updatePasswordRequest) {

        authService.updatePassword(updatePasswordRequest);

        return ResponseEntity.ok(new MessageResponse(Constants.PASSWORD_SUCCESSFULLY_UPDATED));
    }

    @PutMapping("/forgot-password")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Object> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {

        authService.forgotPassword(forgotPasswordRequest);

        return ResponseEntity.ok("Mail başarıyla gönderildi");
    }

    @PutMapping("/reset-password")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Boolean> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {

        Boolean reset = authService.resetPassword(resetPasswordRequest);

        return ResponseEntity.ok(reset);
    }

    @PostMapping("/verify")
    public ResponseEntity<JwtResponse> verifyToken() {
        User user = authService.getCurrentUser();
        
        // User'ın rollerini al
        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .toList();
        
        JwtResponse response = new JwtResponse();
        response.setId(user.getId().toString());
        response.setUsername(user.getUserName());
        response.setSurname(user.getUserSurname());
        response.setEmail(user.getEmail());
        response.setRoles(roles);
        response.setPhone(user.getPhone());
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/new-password")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Object> newPassword(@Valid @RequestBody NewPasswordRequest newPasswordRequest) {

        authService.newPassword(newPasswordRequest);

        return ResponseEntity.ok("Şifre başarıyla oluşturuldu");
    }

    @PostMapping("logout")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Object> logout() {
        userTransactionService.logout();
        return ResponseEntity.ok().build();
    }

}
