package com.all4dev.catalog.controller.product;

import com.all4dev.catalog.domain.Product;
import com.all4dev.catalog.domain.ProductDetail;
import org.mapstruct.*;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", imports = UUID.class)
public interface ProductControllerMapper {

    @Mapping(target = "id", source = "request.id")
    @Mapping(target = "price", source = "request.price")
    @Mapping(target = "taxPercent", source = "request.taxPercent")
    Product mapCreateProductRequestToProduct(CreateProductRequest request);

    @Mapping(target = "productId", source = "request.id")
    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "locale", source = "request.locale")
    ProductDetail mapCreateProductRequestToProductDetail(CreateProductRequest request);

    @Mapping(target = "id", source = "product.id")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "taxPercent", source = "product.taxPercent")
    @Mapping(target = "name", source = "detail.name")
    @Mapping(target = "description", source = "detail.description")
    @Mapping(target = "locale", source = "detail.locale")
    ProductResponse mapCreateProductAndDetailToProductResponse(Product product, ProductDetail detail);

    @Mapping(target = "price", source = "request.price")
    @Mapping(target = "taxPercent", source = "request.taxPercent")
    @Mapping(target = "id", ignore = true)
    Product mapPatchProductRequestToProduct(@Context Long productId, PatchProductRequest request);

    @AfterMapping
    default void mapPatchProductRequestToProduct(@MappingTarget Product target, @Context Long productId) {
        target.setId(productId);
    }

    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "locale", source = "request.locale")
    @Mapping(target = "productId", ignore = true)
    ProductDetail mapPatchProductRequestToProductDetail(@Context Long productId, PatchProductRequest request);

    @AfterMapping
    default void mapPatchProductRequestToProductDetail(@MappingTarget ProductDetail target, @Context Long productId) {
        target.setProductId(productId);
    }

    List<ProductResponse> mapProductListToProductResponseList(List<Product> products);
}
