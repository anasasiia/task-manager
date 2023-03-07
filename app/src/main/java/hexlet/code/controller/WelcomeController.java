package hexlet.code.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/welcome")
public class WelcomeController {

    @GetMapping(path = "")
    public void sayWelcome() {
        System.out.println("Welcome to Spring");
    }
}
