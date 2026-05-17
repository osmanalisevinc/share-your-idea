package com.share.security.services;


import com.share.constants.Constants;
import com.share.controller.authcontroller.request.*;
import com.share.controller.authcontroller.response.JwtResponse;
import com.share.exception.BadRequestException;
import com.share.exception.ResourceNotFoundException;
import com.share.model.PasswordResetCode;
import com.share.model.Role;
import com.share.model.User;
import com.share.model.enums.ERole;
import com.share.repository.EmailSender;
import com.share.repository.PasswordResetRepository;
import com.share.repository.RoleRepository;
import com.share.repository.UserRepository;
import com.share.security.jwt.JwtUtils;
import com.share.service.EmailService;
import com.share.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetService passwordResetService;
    private final EmailSender emailSender;
    private final EmailService emailService;
    private final PasswordResetRepository passwordResetRepository;

    private static final Random random = new Random();

    @Transactional
    //  @CacheEvict(value = "users", key = "'allUsers'")
    public void register(@Valid SignupRequest signUpRequest, MultipartFile file) {

        if (Boolean.TRUE.equals(userRepository.existsByEmail(signUpRequest.getEmail()))) {
            throw new IllegalArgumentException(String.format(Constants.EMAIL_ALREADY_EXISTS, signUpRequest.getEmail()));
        }

        // Create new user's account
        User user = new User();
        user.setUserName(userNameSaveFormat(signUpRequest.getUserName()));
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setUserSurname(userNameSaveFormat(signUpRequest.getUserSurname()));
        user.setCreatedDate(LocalDateTime.now());
        user.setProfilePhoto(null);
        user.setPhone(signUpRequest.getPhone());

        Set<Role> roles = new HashSet<>();

        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException(String.format(Constants.ROLE_NOT_FOUND, ERole.ROLE_USER)));
        roles.add(userRole);

        user.setRoles(roles);
        userRepository.save(user);
    }

    public JwtResponse signIn(@Valid LoginRequest loginRequest) {
        boolean existsEmail = userRepository.existsByEmail(loginRequest.getEmail());
        if (!existsEmail) {
            throw new BadRequestException(String.format(Constants.EMAIL_NOT_FOUND, loginRequest.getEmail()));
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();


        return new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getSurname(),
                userDetails.getEmail(),
                roles,
                userDetails.getPhone());
    }

    @Transactional
    public void updatePassword(@Valid UpdatePasswordRequest updatePasswordRequest) {
        User user = getCurrentUser();

        if (!passwordEncoder.matches(updatePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new BadRequestException(Constants.PASSWORD_NOT_MISMATCH);
        }

        String hashedPassword = passwordEncoder.encode(updatePasswordRequest.getNewPassword());
        user.setPassword(hashedPassword);
        userRepository.save(user);
    }

    public User getCurrentUser() {

        String email = SecurityUtils.getCurrentUserLogin().orElseThrow(() ->
                new ResourceNotFoundException(Constants.USER_NOT_FOUND));

        return getUserByEmail(email);

    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException(Constants.USER_NOT_FOUND));
    }

    public String userNameSaveFormat(String username) {

        String[] words = username.split("\\s+");
        StringBuilder formattedName = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                formattedName.append(word.substring(0, 1).toUpperCase())
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }

        return formattedName.toString().trim();
    }

    public void forgotPassword(@Valid ForgotPasswordRequest forgotPasswordRequest) {
        User user = userRepository.findByEmail(forgotPasswordRequest.getEmail()).orElseThrow(() ->
                new ResourceNotFoundException(Constants.USER_NOT_FOUND));

        String code = codeGenerate();
        PasswordResetCode passwordResetToken = new PasswordResetCode(code, LocalDateTime.now(), LocalDateTime.now().plusDays(1), user);
        passwordResetService.savePasswordResetToken(passwordResetToken);
        userRepository.save(user);
        emailSender.send(
                user.getEmail(),
                emailService.buildForgotPasswordEmail(user.getUserName(), code));

    }


    public static String codeGenerate() {

        StringBuilder kod = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            // 0-9 arasında rastgele bir rakam seç
            int rakam = random.nextInt(10);
            kod.append(rakam);
        }

        return kod.toString();
    }

    @Transactional
    public Boolean resetPassword(@Valid ResetPasswordRequest resetPasswordRequest) {
        User user = userRepository.findByEmail(resetPasswordRequest.getEmail()).orElseThrow(() ->
                new ResourceNotFoundException(Constants.USER_NOT_FOUND));

        Optional<PasswordResetCode> resetCode = passwordResetRepository.findByUserAndCode(user, resetPasswordRequest.getCode());

        if (resetCode.isPresent()) {
            LocalDateTime expiresAt = resetCode.get().getExpiresAt();

            if (LocalDateTime.now().isAfter(expiresAt)) {
                throw new BadRequestException("Kodun süresi doldu lütfen yeni kod alınız");
            }
            user.setPassword(null);

            userRepository.save(user);

            return true;
        }

        return false;
    }

    @Transactional
    public void newPassword(@Valid NewPasswordRequest newPasswordRequest) {
        if (!newPasswordRequest.getPassword().equals(newPasswordRequest.getPasswordAgain())) {
            throw new IllegalArgumentException("Şifreler birbiriyle uyuşmuyor");
        }
        User user = userRepository.findByEmail(newPasswordRequest.getEmail()).orElseThrow(() ->
                new ResourceNotFoundException(Constants.USER_NOT_FOUND));

        user.setPassword(passwordEncoder.encode(newPasswordRequest.getPassword()));

        userRepository.save(user);
    }
}
