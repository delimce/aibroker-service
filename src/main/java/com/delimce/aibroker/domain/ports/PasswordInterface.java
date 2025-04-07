package com.delimce.aibroker.domain.ports;

public interface PasswordInterface {
    /**
     * Encodes a raw password
     * @param rawPassword the password to encode
     * @return the encoded password
     */
    String encode(String rawPassword);
    
    /**
     * Checks if a raw password matches an encoded password
     * @param rawPassword the raw password to check
     * @param encodedPassword the encoded password to check against
     * @return true if the passwords match, false otherwise
     */
    boolean matches(String rawPassword, String encodedPassword);
}
