package com.example.controller;

import com.example.service.CallService;
import com.example.service.ServiceA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class ControllerA {

    private final CallService service;

    @Autowired
    public ControllerA(@Qualifier("serviceB") CallService service) {
        this.service = service;
    }

    @GetMapping("test/{param}")
    public ResponseEntity<String> get(@PathVariable("param") final String param) {

        return ResponseEntity.ok().body(service.test(param));

    }

}
