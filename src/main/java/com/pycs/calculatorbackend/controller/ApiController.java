package com.pycs.calculatorbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pycs.calculatorbackend.configuration.JwtTokenUtil;
import com.pycs.calculatorbackend.model.JwtRequest;
import com.pycs.calculatorbackend.model.JwtResponse;
import com.pycs.calculatorbackend.model.Response;
import com.pycs.calculatorbackend.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author njagi
 * @Date 24/06/2023
 */
@CrossOrigin("*")
@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private Response apiResponse = new Response();

    private void authenticate(String username, String password) throws AuthenticationException {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new DisabledException("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("INVALID_CREDENTIALS", e);
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
        try {
            String authUsername = authenticationRequest.getUsername();
            String authPassword = authenticationRequest.getPassword();
            System.out.println("username received ## " + authenticationRequest.getUsername());
            authenticate(authUsername, authPassword);
            final UserDetails userDetails = userDetailsService.loadUserByUsername(authUsername);
            final String token = jwtTokenUtil.generateToken(userDetails);
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (DisabledException e) {
            apiResponse.setReturnCode("01");
            apiResponse.setMessage("oops! internal error occurred");
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (BadCredentialsException e) {
            apiResponse.setReturnCode("01");
            apiResponse.setMessage("oops! invalid credentials");
            return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED);
        } catch (AuthenticationException e) {
            apiResponse.setReturnCode("01");
            apiResponse.setMessage("oops! internal error occurred");
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

