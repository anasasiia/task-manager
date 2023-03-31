package hexlet.code.controller;

import com.rollbar.notifier.Rollbar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/welcome")
public final class WelcomeController {

    @Autowired
    private Rollbar rollbar;

    @GetMapping(path = "")
    public void sayWelcome() {
        rollbar.debug("Here is some debug message");
        System.out.println("Welcome to Spring");
    }
}
