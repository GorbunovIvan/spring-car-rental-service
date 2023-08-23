package org.example.repository;

import org.example.entity.deals.ProductCard;
import org.springframework.lang.NonNull;

public interface ProductCardRepositoryCustom {

    ProductCard merge(@NonNull ProductCard productCard);
}
