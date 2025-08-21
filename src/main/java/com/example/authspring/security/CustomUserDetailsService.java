package com.example.authspring.security;

import com.example.authspring.domain.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{

        var u = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("not found"));

        var authorities = u.getRoles().stream()
                .map(r -> "ROLE_" + r.name()).map(org.springframework.security.core.authority.SimpleGrantedAuthority::new)
                .toList();

        return new org.springframework.security.core.userdetails.User(u.getUsername(), u.getPassword(), authorities);
    }
}
