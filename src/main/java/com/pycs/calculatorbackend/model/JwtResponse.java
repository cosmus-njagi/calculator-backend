package com.pycs.calculatorbackend.model;

import java.io.Serializable;

/**
 * @author njagi
 * @Date 24/06/2023
 */

public class JwtResponse implements Serializable {
    private static final long serialVersionUID = -8091879091924046844L;
    private final String jwttoken;

    public JwtResponse(String jwttoken) {
        this.jwttoken = jwttoken;
    }

    public String getToken() {
        return this.jwttoken;
    }
}

