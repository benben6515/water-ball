package tw.waterballsa.model;

import jakarta.persistence.*;
import tw.waterballsa.security.EncryptionConverter;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * User entity representing a registered user in the Water Ball Platform.
 * Contains encrypted PII fields (email, birthday, location) stored as BYTEA in PostgreSQL.
 *
 * @author Water Ball SA
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    /**
     * Encrypted email stored as BYTEA. Use getEmail()/setEmail() to access the plaintext value.
     * The EncryptionConverter automatically encrypts/decrypts this field.
     */
    @Column(name = "email_encrypted", nullable = false, columnDefinition = "BYTEA")
    @Convert(converter = EncryptionConverter.class)
    private String email;

    /**
     * SHA-256 hash of the email for uniqueness constraint and lookups.
     * Automatically computed when email is set.
     */
    @Column(name = "email_hash", nullable = false, unique = true, length = 64)
    private String emailHash;

    @Column(name = "gender", length = 20)
    private String gender; // 男性, 女性, 其他, 不透露

    /**
     * Encrypted birthday stored as BYTEA. Use getBirthday()/setBirthday() to access the plaintext value.
     */
    @Column(name = "birthday_encrypted", columnDefinition = "BYTEA")
    @Convert(converter = EncryptionConverter.class)
    private String birthday; // Stored as ISO date string (YYYY-MM-DD)

    /**
     * Encrypted location stored as BYTEA. Use getLocation()/setLocation() to access the plaintext value.
     */
    @Column(name = "location_encrypted", columnDefinition = "BYTEA")
    @Convert(converter = EncryptionConverter.class)
    private String location;

    @Column(name = "occupation", length = 100)
    private String occupation;

    @Column(name = "github_link", length = 255)
    private String githubLink;

    @Column(name = "level", nullable = false)
    private Integer level = 1;

    /**
     * Total experience points accumulated by the user.
     * Level is calculated based on this value using the exp table.
     */
    @Column(name = "exp", nullable = false)
    private Integer exp = 0;

    /**
     * User role for role-based access control (RBAC).
     * Default: STUDENT
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private UserRole role = UserRole.STUDENT;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * OAuth provider links associated with this user.
     * Cascade operations are not used to prevent accidental deletion.
     */
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<OAuthProviderLink> oauthProviderLinks = new ArrayList<>();

    // Constructors

    public User() {
    }

    public User(String nickname, String email, String gender, LocalDate birthday, String location, String occupation, String githubLink) {
        this.nickname = nickname;
        setEmail(email); // This will also set emailHash
        this.gender = gender;
        setBirthday(birthday);
        this.location = location;
        this.occupation = occupation;
        this.githubLink = githubLink;
    }

    // Lifecycle callbacks

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.exp == null) {
            this.exp = 0;
        }
        if (this.level == null) {
            this.level = calculateLevelFromExp(this.exp);
        }
        if (this.role == null) {
            this.role = UserRole.STUDENT;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    /**
     * Set email and automatically compute SHA-256 hash for email_hash column.
     * The email will be encrypted by EncryptionConverter before database storage.
     */
    public void setEmail(String email) {
        this.email = email;
        if (email != null && !email.isEmpty()) {
            this.emailHash = computeEmailHash(email);
        }
    }

    public String getEmailHash() {
        return emailHash;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Get birthday as LocalDate. Returns null if birthday is not set.
     */
    public LocalDate getBirthday() {
        if (birthday == null || birthday.isEmpty()) {
            return null;
        }
        return LocalDate.parse(birthday);
    }

    /**
     * Set birthday from LocalDate. The date will be stored as ISO string (YYYY-MM-DD)
     * and encrypted by EncryptionConverter.
     */
    public void setBirthday(LocalDate birthday) {
        this.birthday = (birthday != null) ? birthday.toString() : null;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getGithubLink() {
        return githubLink;
    }

    public void setGithubLink(String githubLink) {
        this.githubLink = githubLink;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getExp() {
        return exp;
    }

    public void setExp(Integer exp) {
        this.exp = exp;
        // Auto-update level when exp changes
        this.level = calculateLevelFromExp(exp);
    }

    /**
     * Add experience points and auto-level up if needed.
     * @param amount Amount of exp to add
     * @return true if leveled up, false otherwise
     */
    public boolean addExp(int amount) {
        int oldLevel = this.level;
        this.exp += amount;
        this.level = calculateLevelFromExp(this.exp);
        return this.level > oldLevel;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<OAuthProviderLink> getOauthProviderLinks() {
        return oauthProviderLinks;
    }

    public void setOauthProviderLinks(List<OAuthProviderLink> oauthProviderLinks) {
        this.oauthProviderLinks = oauthProviderLinks;
    }

    // Helper methods

    /**
     * Compute SHA-256 hash of email for email_hash column.
     * This hash is used for uniqueness constraint and lookups without exposing the encrypted email.
     */
    private String computeEmailHash(String email) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(email.toLowerCase().getBytes(StandardCharsets.UTF_8));

            // Convert to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Add an OAuth provider link to this user.
     */
    public void addOAuthProviderLink(OAuthProviderLink link) {
        oauthProviderLinks.add(link);
        link.setUser(this);
    }

    /**
     * Remove an OAuth provider link from this user.
     */
    public void removeOAuthProviderLink(OAuthProviderLink link) {
        oauthProviderLinks.remove(link);
        link.setUser(null);
    }

    // Experience and Level calculation methods

    /**
     * Calculate level based on total experience points.
     * Based on the exp table in docs/student-exp.md
     */
    private static int calculateLevelFromExp(int totalExp) {
        // Level thresholds from student-exp.md
        int[] expThresholds = {
            0,     // Level 1
            200,   // Level 2
            500,   // Level 3
            1500,  // Level 4
            3000,  5000,  7000,  9000,  11000, 13000,  // Levels 5-10
            15000, 17000, 19000, 21000, 23000,          // Levels 11-15
            25000, 27000, 29000, 31000, 33000,          // Levels 16-20
            35000, 37000, 39000, 41000, 43000,          // Levels 21-25
            45000, 47000, 49000, 51000, 53000,          // Levels 26-30
            55000, 57000, 59000, 61000, 63000, 65000    // Levels 31-36
        };

        for (int i = expThresholds.length - 1; i >= 0; i--) {
            if (totalExp >= expThresholds[i]) {
                return i + 1; // Level is index + 1
            }
        }
        return 1; // Default to level 1
    }

    /**
     * Get experience needed for next level.
     * Returns -1 if at max level (36)
     */
    public int getExpForNextLevel() {
        if (level >= 36) {
            return -1; // Max level reached
        }

        int[] expThresholds = {
            0,     // Level 1
            200,   // Level 2
            500,   // Level 3
            1500,  // Level 4
            3000,  5000,  7000,  9000,  11000, 13000,  // Levels 5-10
            15000, 17000, 19000, 21000, 23000,          // Levels 11-15
            25000, 27000, 29000, 31000, 33000,          // Levels 16-20
            35000, 37000, 39000, 41000, 43000,          // Levels 21-25
            45000, 47000, 49000, 51000, 53000,          // Levels 26-30
            55000, 57000, 59000, 61000, 63000, 65000    // Levels 31-36
        };

        return expThresholds[level] - exp;
    }

    /**
     * Get progress percentage to next level (0-100).
     * Returns 100 if at max level.
     */
    public int getExpProgressPercentage() {
        if (level >= 36) {
            return 100; // Max level
        }

        int[] expThresholds = {
            0,     // Level 1
            200,   // Level 2
            500,   // Level 3
            1500,  // Level 4
            3000,  5000,  7000,  9000,  11000, 13000,  // Levels 5-10
            15000, 17000, 19000, 21000, 23000,          // Levels 11-15
            25000, 27000, 29000, 31000, 33000,          // Levels 16-20
            35000, 37000, 39000, 41000, 43000,          // Levels 21-25
            45000, 47000, 49000, 51000, 53000,          // Levels 26-30
            55000, 57000, 59000, 61000, 63000, 65000    // Levels 31-36
        };

        int currentLevelExp = expThresholds[level - 1];
        int nextLevelExp = expThresholds[level];
        int expInCurrentLevel = exp - currentLevelExp;
        int expNeededForLevel = nextLevelExp - currentLevelExp;

        return (int) ((expInCurrentLevel * 100.0) / expNeededForLevel);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", nickname='" + nickname + '\'' +
                ", emailHash='" + emailHash + '\'' +
                ", gender='" + gender + '\'' +
                ", level=" + level +
                ", role=" + role +
                ", createdAt=" + createdAt +
                '}';
    }
}
