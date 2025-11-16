package tw.waterballsa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.waterballsa.model.OAuthProviderLink;
import tw.waterballsa.model.OAuthProviderLink.ProviderType;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for OAuthProviderLink entity.
 * Provides CRUD operations and custom query methods for OAuth provider management.
 *
 * @author Water Ball SA
 */
@Repository
public interface OAuthProviderLinkRepository extends JpaRepository<OAuthProviderLink, Long> {

    /**
     * Find an OAuth provider link by provider type and provider user ID.
     * This method is used during OAuth login to check if a provider account is already linked
     * to a user in our system.
     *
     * The combination of (provider_type, provider_user_id) is unique due to the database constraint.
     *
     * @param providerType the OAuth provider type (GOOGLE or FACEBOOK)
     * @param providerUserId the unique user ID from the OAuth provider
     * @return Optional containing the OAuthProviderLink if found, empty otherwise
     */
    Optional<OAuthProviderLink> findByProviderTypeAndProviderUserId(ProviderType providerType, String providerUserId);

    /**
     * Find all OAuth provider links for a specific user.
     * This method returns all OAuth accounts (Google, Facebook) linked to a user.
     *
     * @param userId the ID of the user
     * @return List of OAuthProviderLink entities for this user
     */
    List<OAuthProviderLink> findByUser_UserId(Long userId);

    /**
     * Find OAuth provider links by provider email.
     * This method is used for account merging - when a user logs in with a different provider
     * but uses the same email, we can find their existing account.
     *
     * @param providerEmail email address from the OAuth provider
     * @return List of OAuthProviderLink entities with this email
     */
    List<OAuthProviderLink> findByProviderEmail(String providerEmail);

    /**
     * Check if an OAuth provider link exists for a given provider type and provider user ID.
     *
     * @param providerType the OAuth provider type (GOOGLE or FACEBOOK)
     * @param providerUserId the unique user ID from the OAuth provider
     * @return true if the provider link exists, false otherwise
     */
    boolean existsByProviderTypeAndProviderUserId(ProviderType providerType, String providerUserId);
}
