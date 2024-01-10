package com.bezro.shopRESTfulAPI.dtos;

import lombok.Data;

@Data
public class JwtRequest {
    private String username;
    private String password;

}
