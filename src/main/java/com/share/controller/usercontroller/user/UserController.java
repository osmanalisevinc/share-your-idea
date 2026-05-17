package com.share.controller.usercontroller.user;


import com.share.controller.usercontroller.user.request.UpdateUserRequest;
import com.share.model.User;
import com.share.model.dto.MessageResponse;
import com.share.model.dto.UserDTO;
import com.share.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserDTO> get() {
        UserDTO userDTO = userService.getUser();

        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserDTO> getUserById(
            @PathVariable String id
    ) {
        User user = userService.getUserById(id);

        UserDTO userDTO = new UserDTO(user);
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserDTO> update(@Valid @RequestPart UpdateUserRequest updateUserRequest,
                                          @RequestPart(required = false) MultipartFile photo) throws IOException {

        User user = userService.updateUser(updateUserRequest, photo);

        UserDTO userDTO = new UserDTO(user);

        return ResponseEntity.ok(userDTO);
    }

    @DeleteMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Object> deleteMyAccount() {
        userService.deleteMyAccount();

        return ResponseEntity.ok(new MessageResponse("Hesabınız başarıyla silindi"));
    }


}
