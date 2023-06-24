package com.pycs.calculatorbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pycs.calculatorbackend.configuration.JwtTokenUtil;
import com.pycs.calculatorbackend.model.JwtRequest;
import com.pycs.calculatorbackend.model.JwtResponse;
import com.pycs.calculatorbackend.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author njagi
 * @Date 24/06/2023
 */
@RestController
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authRequest) throws Exception {
        System.out.println("Request params ###"+ new ObjectMapper().writeValueAsString(authRequest));
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new Exception("Invalid username or password");
        }

        final UserDetails userDetails = (UserDetails) userDetailsService
                .loadUserByUsername(authRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }
}

