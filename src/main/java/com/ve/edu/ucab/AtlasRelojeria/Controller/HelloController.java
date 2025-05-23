package com.ve.edu.ucab.AtlasRelojeria.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HelloController {

    @GetMapping("/app/foo")
    public Map<String,String> foo(){
        Map<String, String> json = new HashMap<>();
        json.put("message", "Hola Ingeniera de Software");

        return json;
    }
}
