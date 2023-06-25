package com.pycs.calculatorbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Collections;

/**
 * @author njagi
 * @Date 24/06/2023
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // static username, password
        String user = "cosmus";
        String password = "P@ssw0rd";  // Plain text password

        if (user == null) {
            System.out.println("User not found");
            throw new UsernameNotFoundException("Username not found");
        }

        String encodedPassword = passwordEncoder.encode(password);

        return new org.springframework.security.core.userdetails.User(username, encodedPassword, Collections.emptyList());
    }
}






