package com.share.controller.sharingcontroller;

import com.share.controller.sharingcontroller.request.CreateSharingRequest;
import com.share.controller.sharingcontroller.response.SharingResponse;
import com.share.service.SharingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/sharing")
@RequiredArgsConstructor
public class SharingController {
    private final SharingService sharingService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String createSharing(
            @RequestBody CreateSharingRequest createSharingRequest
    ) {
        sharingService.createSharing(createSharingRequest);

        return ResponseEntity.ok("Paylaşım başarıyla oluşturuldu").getBody();
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<SharingResponse>> getAllSharing() {
        return ResponseEntity.ok(sharingService.getAllSharing());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<String> deleteSharing(@PathVariable String id) {
        sharingService.deleteSharing(id);
        return ResponseEntity.ok("Sharing deleted successfully");
    }

    @DeleteMapping("/expire/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<String> deleteSharingExpireAt(@PathVariable String id) {
        sharingService.deleteSharingExpire(id);
        return ResponseEntity.ok("Sharing deleted successfully");
    }

    @GetMapping("/my-shares")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<SharingResponse>> getMyShares() {
        List<SharingResponse> myShares = sharingService.getMyShares();

        return ResponseEntity.ok(myShares);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<SharingResponse>> getSharesByUser(
            @PathVariable String userId
    ) {
        List<SharingResponse> myShares = sharingService.getUserShares(userId);

        return ResponseEntity.ok(myShares);
    }

    @GetMapping("/my-following-shares")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<SharingResponse>> getMyFollowingShares() {
        List<SharingResponse> myShares = sharingService.getMyFollowingShares();

        return ResponseEntity.ok(myShares);
    }

    @PostMapping("/expire/{sharingId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<String> expireSharing(@PathVariable String sharingId) {
        sharingService.startExpire(sharingId);
        return ResponseEntity.ok("Sharing expire successfully");
    }

}
