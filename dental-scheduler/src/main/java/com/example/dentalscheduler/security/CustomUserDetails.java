package com.example.dentalscheduler.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@Setter
public class CustomUserDetails extends User {

    private Long id;
    private com.example.dentalscheduler.model.User user;

    public CustomUserDetails(String username,
                             String password,
                             Collection<? extends GrantedAuthority> authorities,
                             Long id,
                             com.example.dentalscheduler.model.User user) {
        super(username, password, authorities);
        this.id = id;
        this.user = user;
    }
}
