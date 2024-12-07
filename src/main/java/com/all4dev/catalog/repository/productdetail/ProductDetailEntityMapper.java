package com.all4dev.catalog.repository.productdetail;

import com.all4dev.catalog.domain.ProductDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", imports = UUID.class)
public interface ProductDetailEntityMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productId", source = "productDetail.productId")
    @Mapping(target = "name", source = "productDetail.name")
    @Mapping(target = "description", source = "productDetail.description")
    @Mapping(target = "locale", source = "productDetail.locale")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    ProductDetailEntity mapProductDetailToProductDetailEntity(ProductDetail productDetail);

    ProductDetail mapProductDetailEntityToProductDetail(ProductDetailEntity productDetailEntity);

    List<ProductDetail> mapProductDetailEntitiesToProductDetails(List<ProductDetailEntity> productDetailEntities);
}
