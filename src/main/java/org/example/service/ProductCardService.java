package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.deals.Address;
import org.example.entity.deals.ProductCard;
import org.example.repository.ProductCardRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductCardService extends BasicService<ProductCard, Long> {

    private final ProductCardRepository productCardRepository;

    @Override
    protected JpaRepository<ProductCard, Long> getRepository() {
        return productCardRepository;
    }

    public List<ProductCard> getAllAvailable() {
        return getAll().stream()
                .filter(p -> !p.isLeased())
                .toList();
    }

    public Address getOrCreateAddressByProperties(String country, String town, String street, String buildingNumber) {
        return productCardRepository.findAddressByFields(country, town, street, buildingNumber)
                .orElse(new Address(country, town, street, buildingNumber));
    }

    @Override
    public ProductCard create(ProductCard productCard) {
        return productCardRepository.merge(productCard);
    }
}
