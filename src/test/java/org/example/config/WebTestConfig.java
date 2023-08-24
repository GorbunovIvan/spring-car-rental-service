package org.example.config;

import lombok.RequiredArgsConstructor;
import org.example.entity.car.Car;
import org.example.entity.car.CarBrand;
import org.example.entity.deals.Address;
import org.example.entity.user.UserType;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableWebMvc
public class WebTestConfig implements WebMvcConfigurer {

    private final Converter<String, Car> carConverter;
    private final Converter<String, CarBrand> carBrandConverter;
    private final Converter<String, Address> addressConverter;
    private final Converter<String, UserType> userTypeConverter;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(carConverter);
        registry.addConverter(carBrandConverter);
        registry.addConverter(addressConverter);
        registry.addConverter(userTypeConverter);
    }
}
