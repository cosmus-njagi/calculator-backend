package com.pycs.calculatorbackend.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if ("cosmus".equals(username)) {
            // Hardcoded password for the username
            String password = "password";
            return new org.springframework.security.core.userdetails.User(
                    username,
                    password,
                    Collections.emptyList()
            );
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }
}
