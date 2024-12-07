package com.all4dev.catalog.repository.productdetail;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Locale;

interface ProductDetailJPARepository extends CrudRepository<ProductDetailEntity, Long> {
    ProductDetailEntity findByProductIdAndLocale(Long productId, Locale locale);

    List<ProductDetailEntity> findAllByLocale(Locale locale);
}
