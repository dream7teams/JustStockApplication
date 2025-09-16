package net.juststock.trading.security;

import java.util.Set;

public class UserPrincipal {
    private final Long id;
    private final String fullName;
    private final String phone;
    private final Set<String> roles;

    public UserPrincipal(Long id, String fullName, String phone, Set<String> roles) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.roles = roles;
    }
    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public Set<String> getRoles() { return roles; }
}
