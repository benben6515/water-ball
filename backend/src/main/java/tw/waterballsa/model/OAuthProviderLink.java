package tw.waterballsa.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * OAuthProviderLink entity representing a link between a User and an OAuth provider (Google, Facebook).
 * Each user can have multiple OAuth providers linked, but each provider account can only link to one user.
 *
 * @author Water Ball SA
 */
@Entity
@Table(
    name = "oauth_provider_link",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_oauth_provider_uid",
            columnNames = {"provider_type", "provider_user_id"}
        )
    }
)
public class OAuthProviderLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "provider_link_id")
    private Long providerLinkId;

    /**
     * The user this OAuth provider is linked to.
     * ON DELETE RESTRICT prevents deletion of users with OAuth links.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_oauth_user"))
    private User user;

    /**
     * The OAuth provider type (e.g., "google", "facebook").
     * Together with provider_user_id, this uniquely identifies an OAuth account.
     */
    @Column(name = "provider_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ProviderType providerType;

    /**
     * The unique user ID from the OAuth provider.
     * This is the "sub" claim from Google or the "id" from Facebook.
     */
    @Column(name = "provider_user_id", nullable = false, length = 255)
    private String providerUserId;

    /**
     * The email address from the OAuth provider.
     * Used for account merging when a user logs in with a different provider using the same email.
     */
    @Column(name = "provider_email", nullable = false, length = 255)
    private String providerEmail;

    /**
     * Timestamp when this OAuth provider was linked to the user.
     */
    @Column(name = "linked_at", nullable = false, updatable = false)
    private LocalDateTime linkedAt;

    // Constructors

    public OAuthProviderLink() {
    }

    public OAuthProviderLink(User user, ProviderType providerType, String providerUserId, String providerEmail) {
        this.user = user;
        this.providerType = providerType;
        this.providerUserId = providerUserId;
        this.providerEmail = providerEmail;
    }

    // Lifecycle callbacks

    @PrePersist
    protected void onCreate() {
        this.linkedAt = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getProviderLinkId() {
        return providerLinkId;
    }

    public void setProviderLinkId(Long providerLinkId) {
        this.providerLinkId = providerLinkId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ProviderType getProviderType() {
        return providerType;
    }

    public void setProviderType(ProviderType providerType) {
        this.providerType = providerType;
    }

    public String getProviderUserId() {
        return providerUserId;
    }

    public void setProviderUserId(String providerUserId) {
        this.providerUserId = providerUserId;
    }

    public String getProviderEmail() {
        return providerEmail;
    }

    public void setProviderEmail(String providerEmail) {
        this.providerEmail = providerEmail;
    }

    public LocalDateTime getLinkedAt() {
        return linkedAt;
    }

    @Override
    public String toString() {
        return "OAuthProviderLink{" +
                "providerLinkId=" + providerLinkId +
                ", providerType=" + providerType +
                ", providerUserId='" + providerUserId + '\'' +
                ", providerEmail='" + providerEmail + '\'' +
                ", linkedAt=" + linkedAt +
                '}';
    }

    /**
     * Enum representing supported OAuth provider types.
     */
    public enum ProviderType {
        GOOGLE("google"),
        FACEBOOK("facebook");

        private final String value;

        ProviderType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        /**
         * Convert string value to ProviderType enum.
         */
        public static ProviderType fromValue(String value) {
            for (ProviderType type : ProviderType.values()) {
                if (type.value.equalsIgnoreCase(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Invalid provider type: " + value);
        }
    }
}
