package com.bezro.shopRESTfulAPI.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class AuthController {
    @GetMapping("/unsecured")
    public String unsecuredData() {
        return "Unsecured data";
    }
    @GetMapping("/secured")
    public String securedData() {
        return "Secured data";
    }
}
