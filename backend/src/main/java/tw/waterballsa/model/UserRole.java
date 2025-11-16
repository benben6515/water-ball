package tw.waterballsa.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * User roles for Discord-like role-based access control.
 * Hierarchy: ADMIN > TEACHER > STUDENT > GUEST
 *
 * @author Water Ball SA
 */
public enum UserRole {
    /**
     * Guest users - limited access to homepage and preview content
     */
    GUEST("訪客", "#6B7280", 0),

    /**
     * Student users - can access purchased courses and submit homework
     */
    STUDENT("學生", "#3B82F6", 1),

    /**
     * Teacher users - can view all courses and grade homework
     */
    TEACHER("老師", "#8B5CF6", 2),

    /**
     * Admin users - full access including role management
     */
    ADMIN("管理員", "#EF4444", 3);

    private final String displayName;
    private final String color;
    private final int hierarchy;

    UserRole(String displayName, String color, int hierarchy) {
        this.displayName = displayName;
        this.color = color;
        this.hierarchy = hierarchy;
    }

    /**
     * Get display name in Traditional Chinese
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get color for Discord-style badge (hex color code)
     */
    public String getColor() {
        return color;
    }

    /**
     * Get role hierarchy level (higher number = more permissions)
     */
    public int getHierarchy() {
        return hierarchy;
    }

    /**
     * Convert to Spring Security GrantedAuthority
     * @return GrantedAuthority with ROLE_ prefix
     */
    public GrantedAuthority toAuthority() {
        return new SimpleGrantedAuthority("ROLE_" + this.name());
    }

    /**
     * Get Spring Security authority name
     * @return Authority name with ROLE_ prefix (e.g., "ROLE_STUDENT")
     */
    public String getAuthority() {
        return "ROLE_" + this.name();
    }

    /**
     * Check if this role has higher or equal privileges than another role
     * @param other The role to compare with
     * @return true if this role has higher or equal hierarchy
     */
    public boolean hasPrivilegeOf(UserRole other) {
        return this.hierarchy >= other.hierarchy;
    }

    /**
     * Get default role for new users
     * @return STUDENT role
     */
    public static UserRole getDefault() {
        return STUDENT;
    }

    /**
     * Parse role from string (case-insensitive)
     * @param role Role name
     * @return UserRole enum
     * @throws IllegalArgumentException if role is invalid
     */
    public static UserRole fromString(String role) {
        if (role == null || role.isBlank()) {
            return getDefault();
        }
        return UserRole.valueOf(role.toUpperCase());
    }
}
