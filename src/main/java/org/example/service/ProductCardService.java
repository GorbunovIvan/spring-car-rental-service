package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.deals.ProductCard;
import org.example.repository.ProductCardRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductCardService extends BasicService<ProductCard, Long> {

    private final ProductCardRepository productCardRepository;

    @Override
    protected JpaRepository<ProductCard, Long> getRepository() {
        return productCardRepository;
    }
}
