package org.example.controller.converters;

import org.example.entity.user.UserType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class UserTypeConverter implements Converter<String, UserType> {

    @Override
    public UserType convert(@Nullable String source) {
        return Arrays.stream(UserType.values())
                .filter(type -> type.getName().equals(source))
                .findAny()
                .orElse(null);
    }
}
