package org.example.controller.converters;

import lombok.RequiredArgsConstructor;
import org.example.entity.deals.Address;
import org.example.service.ProductCardService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class AddressConverter implements Converter<String, Address> {

    private final ProductCardService productCardService;

    @Override
    public Address convert(@NonNull String source) {

        var pattern = Pattern.compile("(.+?), (.+?), (.+?), (.+)");
        var matcher = pattern.matcher(source);

        if (!matcher.find()) {
            return null;
        }

        String country = matcher.group(1).replaceAll(" ", "-");
        String town = matcher.group(2).replaceAll(" ", "-");
        String street = matcher.group(3).replaceAll(" ", "-");
        String buildingNumber = matcher.group(4).replaceAll(" ", "-");

        return productCardService.getOrCreateAddressByProperties(country, town, street, buildingNumber);
    }
}
