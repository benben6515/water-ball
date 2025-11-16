package tw.waterballsa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.waterballsa.model.User;

import java.util.Optional;

/**
 * Repository interface for User entity.
 * Provides CRUD operations and custom query methods for user management.
 *
 * @author Water Ball SA
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by their email hash.
     * The email hash is a SHA-256 hash of the email address, used for uniqueness constraints
     * and lookups without exposing the encrypted email.
     *
     * This method is used during OAuth registration/login to check if a user with the same
     * email already exists, enabling automatic account merging.
     *
     * @param emailHash SHA-256 hash of the email address
     * @return Optional containing the User if found, empty otherwise
     */
    Optional<User> findByEmailHash(String emailHash);

    /**
     * Check if a user with the given email hash exists.
     *
     * @param emailHash SHA-256 hash of the email address
     * @return true if a user with this email hash exists, false otherwise
     */
    boolean existsByEmailHash(String emailHash);
}
