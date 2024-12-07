package com.all4dev.catalog.repository.product;

import com.all4dev.catalog.domain.Product;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", imports = UUID.class)
interface ProductEntityMapper {

    ProductEntity mapProductToProductEntity(Product product);

    Product mapProductEntityToProduct(ProductEntity productEntity);

    List<Product> mapProductEntityListToProductList(List<ProductEntity> sourceList);
}
