package com.example.app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloEndpointController {
    @GetMapping("/hello")
    public String hello(@RequestParam(required = false, defaultValue = "World") String name) {
        if (name.startsWith("execute:")) {
            // SECURITY ALERT: vulnerable to Remote Code Execution since
            // it loads class that an attacker control.
            String className = name.substring(8);
            try {
                Class.forName(className).getConstructor().newInstance();
            } catch (Exception ignored){}
        }
        return "Hello " + name + "!";
    }

    @GetMapping("/secureHello")
    public String secureHello(@RequestParam(required = false, defaultValue = "World") String name) {
        return "Hello " + name + "!";
    }
}
