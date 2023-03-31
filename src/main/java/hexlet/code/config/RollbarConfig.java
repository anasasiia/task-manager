package hexlet.code.config;

import com.rollbar.notifier.Rollbar;
import com.rollbar.notifier.config.Config;
import com.rollbar.spring.webmvc.RollbarSpringConfigBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration()
@ComponentScan(basePackages = "hexlet.code")
public class RollbarConfig {
    @Bean
    public Rollbar rollbar() {
        return new Rollbar(getRollbarConfigs("${rollbar-token}"));
    }
    private Config getRollbarConfigs(String accessToken) {
        // Reference ConfigBuilder.java for all the properties you can set for Rollbar
        return RollbarSpringConfigBuilder.withAccessToken(accessToken)
                .environment("development")
                .build();
    }
}
