package zw.mohcc.org.prep.dto;

import lombok.Getter;

import java.util.Set;

public class JwtResponse {
    private final String token;
    private final Set<String> roles;
    private String username;

    public JwtResponse(String token, Set<String> roles, String username) {
        this.token = token;
        this.roles = roles;
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public String getUsername() {
        return username;
    }
}
