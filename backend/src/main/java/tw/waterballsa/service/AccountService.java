package tw.waterballsa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.waterballsa.model.User;
import tw.waterballsa.repository.UserRepository;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;

/**
 * Account management service
 * Handles account deletion and data removal operations
 */
@Service
public class AccountService {

    @Autowired
    private UserRepository userRepository;

    @Value("${app.base-url:http://localhost:3000}")
    private String baseUrl;

    /**
     * Delete user account and all associated data
     * Required for Facebook Platform Policy compliance
     *
     * @param userId the user ID to delete
     * @return confirmation URL for Facebook Data Deletion Request Callback
     */
    @Transactional
    public String deleteAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("找不到使用者"));

        // TODO: Delete associated data in the following order:
        // 1. User's video progress records
        // 2. User's course enrollments
        // 3. User's orders (mark as deleted, keep for legal compliance)
        // 4. User's achievements
        // 5. User's third-party account links (Discord, GitHub)
        // 6. User's OAuth connections
        // 7. Finally, delete the user record

        // For now, just delete the user
        // In production, you should implement soft delete or archive the data
        userRepository.delete(user);

        // Generate confirmation URL for Facebook
        String confirmationCode = generateConfirmationCode(userId);
        return baseUrl + "/account/deletion?id=" + confirmationCode;
    }

    /**
     * Generate confirmation code for deletion request
     * Required for Facebook Data Deletion Request Callback
     *
     * @param userId the user ID
     * @return hashed confirmation code
     */
    public String generateConfirmationCode(Long userId) {
        try {
            String data = userId + "_" + Instant.now().getEpochSecond();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes());
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("無法生成確認碼", e);
        }
    }
}
