package hexlet.code;

import hexlet.code.config.SpringConfigForIT;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = SpringConfigForIT.class)
class AppApplicationTests {
    @Test
    void contextLoads() {
    }
}
