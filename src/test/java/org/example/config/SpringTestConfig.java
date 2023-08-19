package org.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({WebMvcConfig.class, JpaConfig.class, SecurityConfig.class})
public class SpringTestConfig {
}
