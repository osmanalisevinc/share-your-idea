package com.share.controller.usercontroller.admin;


import com.share.model.User;
import com.share.model.UserTransaction;
import com.share.model.dto.MessageResponse;
import com.share.model.dto.UserDTOAdmin;
import com.share.model.dto.UserTransactionResponse;
import com.share.repository.UserTransactionRepository;
import com.share.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user/admin")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserService userService;
    private final UserTransactionRepository userTransactionRepository;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTOAdmin> getUserById(@PathVariable String id) {
        User user = userService.getUserById(id);
        List<UserTransaction> userTransactions = userTransactionRepository.findByUserId(user.getId());
        List<UserTransactionResponse> transactionResponses = userTransactions.stream()
                .map(UserTransactionResponse::new)
                .toList();
        UserDTOAdmin userDTO = new UserDTOAdmin(user, transactionResponses);

        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/get-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTOAdmin>> getAllUsers() {
        List<UserDTOAdmin> users = userService.getAllUsers();

        return ResponseEntity.ok(users);
    }

    @GetMapping("/pages")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDTOAdmin>> getAllUsersByPage(@RequestParam(required = false) String search,
                                                                @PageableDefault(sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<UserDTOAdmin> users = userService.getUserPage(search, pageable);

        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);

        return ResponseEntity.ok(new MessageResponse("Kullanıcı başarıyla silindi"));
    }


}
