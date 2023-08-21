package org.example.config;

import lombok.RequiredArgsConstructor;
import org.example.controller.converters.AddressConverter;
import org.example.controller.converters.CarBrandConverter;
import org.example.controller.converters.CarConverter;
import org.example.controller.converters.UserTypeConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableWebMvc
public class WebTestConfig implements WebMvcConfigurer {

    private final CarConverter carConverter;
    private final CarBrandConverter carBrandConverter;
    private final AddressConverter addressConverter;
    private final UserTypeConverter userTypeConverter;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(carConverter);
        registry.addConverter(carBrandConverter);
        registry.addConverter(addressConverter);
        registry.addConverter(userTypeConverter);
    }
}
