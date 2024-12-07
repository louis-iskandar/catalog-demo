package com.all4dev.catalog.controller.file;

import com.all4dev.catalog.domain.Product;
import com.all4dev.catalog.domain.ProductDetail;
import org.mapstruct.*;

import java.util.Locale;
import java.util.UUID;

@Mapper(componentModel = "spring", imports = UUID.class)
public interface FileControllerMapper {

    @Mapping(target = "id", source = "product.id")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "taxPercent", source = "product.taxPercent")
    Product mapProductItemToProduct(ProductItem product);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "name", source = "product.name")
    @Mapping(target = "description", source = "product.description")
    @Mapping(target = "locale", ignore = true)
    ProductDetail mapProductItemToProductDetail(ProductItem product, @Context Locale locale);

    @AfterMapping
    default void mapProductItemToProductDetail(@MappingTarget ProductDetail target, @Context Locale locale) {
        target.setLocale(locale);
    }
}
