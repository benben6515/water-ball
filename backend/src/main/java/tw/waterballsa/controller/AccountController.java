package tw.waterballsa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tw.waterballsa.service.AccountService;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for account management operations.
 *
 * Endpoints:
 * - DELETE /api/account - Delete user account and all associated data
 */
@RestController
@RequestMapping("/api/account")
@CrossOrigin(origins = "${cors.allowed-origins}", allowCredentials = "true")
public class AccountController {

    @Autowired
    private AccountService accountService;

    /**
     * DELETE /api/account - Delete user account
     * Permanently deletes the user's account and all associated data.
     * This endpoint is required for Facebook Platform Policy compliance.
     *
     * @param userId the authenticated user ID
     * @return confirmation message with deletion URL (for Facebook compliance)
     */
    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> deleteAccount(@AuthenticationPrincipal Long userId) {
        // Delete account and get confirmation URL
        String confirmationUrl = accountService.deleteAccount(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "帳號已成功刪除");
        response.put("url", confirmationUrl);
        response.put("confirmation_code", accountService.generateConfirmationCode(userId));

        return ResponseEntity.ok(response);
    }
}
