package zw.mohcc.org.prep.dto;

import lombok.Getter;

import java.util.Set;

public class JwtResponse {
    private final String token;
    private final Set<String> roles;

    public JwtResponse(String token, Set<String> roles) {
        this.token = token;
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public Set<String> getRoles() {
        return roles;
    }
}
