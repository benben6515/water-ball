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
        if (this.level == null) {
            this.level = 1;
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
