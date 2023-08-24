package org.example.config;

import lombok.RequiredArgsConstructor;
import org.example.entity.car.Car;
import org.example.entity.car.CarBrand;
import org.example.entity.deals.Address;
import org.example.entity.user.UserType;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;

@Configuration
@ComponentScan(basePackages = "org.example")
@PropertySource("classpath:application.properties")
@EnableWebMvc
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final ApplicationContext applicationContext;

    private final Converter<String, Car> carConverter;
    private final Converter<String, CarBrand> carBrandConverter;
    private final Converter<String, Address> addressConverter;
    private final Converter<String, UserType> userTypeConverter;

    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        var templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setApplicationContext(applicationContext);
        templateResolver.setPrefix("/WEB-INF/views/");
        templateResolver.setSuffix(".html");
        return templateResolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine() {
        var templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        templateEngine.setEnableSpringELCompiler(true);
        return templateEngine;
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        var viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine());
        registry.viewResolver(viewResolver);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(carConverter);
        registry.addConverter(carBrandConverter);
        registry.addConverter(addressConverter);
        registry.addConverter(userTypeConverter);
    }
}
