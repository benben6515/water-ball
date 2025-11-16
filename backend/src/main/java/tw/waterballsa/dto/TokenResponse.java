package tw.waterballsa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Token response DTO returned by POST /auth/refresh endpoint.
 *
 * Contains the new access token and its metadata.
 *
 * Format:
 * {
 *   "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
 *   "token_type": "Bearer",
 *   "expires_in": 900
 * }
 *
 * @author Water Ball SA
 */
public class TokenResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private Long expiresIn; // Seconds until expiration

    public TokenResponse() {
        this.tokenType = "Bearer"; // Default token type
    }

    public TokenResponse(String accessToken, Long expiresIn) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
        this.expiresIn = expiresIn;
    }

    public TokenResponse(String accessToken, String tokenType, Long expiresIn) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
    }

    // Getters and Setters

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }
}
