package tw.waterballsa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Request DTO for PUT /admin/users/{id}/role endpoint.
 *
 * Contains the new role to assign to a user.
 * Only admins can update user roles.
 *
 * Format:
 * {
 *   "role": "STUDENT"
 * }
 *
 * Valid roles: GUEST, STUDENT, TEACHER, ADMIN
 *
 * @author Water Ball SA
 */
public class UpdateUserRoleRequest {

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "^(GUEST|STUDENT|TEACHER|ADMIN)$",
             message = "Role must be one of: GUEST, STUDENT, TEACHER, ADMIN")
    @JsonProperty("role")
    private String role;

    public UpdateUserRoleRequest() {
    }

    public UpdateUserRoleRequest(String role) {
        this.role = role;
    }

    // Getters and Setters

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
