package com.all4dev.catalog.repository.productdetail;

import com.all4dev.catalog.domain.ProductDetail;
import com.all4dev.catalog.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Repository
public class ProductDetailRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductDetailRepository.class);

    final ProductDetailEntityMapper mapper;

    final ProductDetailJPARepository jpaRepository;

    public ProductDetailRepository(ProductDetailEntityMapper mapper, ProductDetailJPARepository jpaRepository) {
        this.mapper = mapper;
        this.jpaRepository = jpaRepository;
    }

    public ProductDetail createProductDetail(ProductDetail productDetail) {
        ProductDetailEntity productDetailEntity = mapper.mapProductDetailToProductDetailEntity(productDetail);
        ProductDetailEntity save = jpaRepository.save(productDetailEntity);
        return mapper.mapProductDetailEntityToProductDetail(save);
    }

    public ProductDetail getProductDetail(Long productId, Locale locale) throws ResourceNotFoundException {
        ProductDetailEntity productDetailEntity = jpaRepository.findByProductIdAndLocale(productId, locale);
        if (productDetailEntity == null) {
            String message = "product detail with product ID: " + productId + " not found!";
            LOGGER.error(message);
            throw new ResourceNotFoundException(message);
        }
        return mapper.mapProductDetailEntityToProductDetail(productDetailEntity);
    }

    public boolean isProductDetailExist(Long productId, Locale locale) {
        ProductDetailEntity entity = jpaRepository.findByProductIdAndLocale(productId, locale);
        return entity != null;
    }

    public ProductDetail udateProductDetail(ProductDetail productDetail) {
        ProductDetailEntity entity = jpaRepository.findByProductIdAndLocale(productDetail.getProductId(), productDetail.getLocale());
        ProductDetailEntity update = mapper.mapProductDetailToProductDetailEntity(productDetail);
        BeanUtils.copyProperties(update, entity, getNullPropertyNames(update));
        ProductDetailEntity saveEntity = jpaRepository.save(entity);
        return mapper.mapProductDetailEntityToProductDetail(saveEntity);
    }

    private String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        emptyNames.add("id");
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    public List<ProductDetail> getAllProductDetails(Locale locale) throws ResourceNotFoundException {
        List<ProductDetailEntity> productDetailEntities = jpaRepository.findAllByLocale(locale);
        if (productDetailEntities.isEmpty()){
            throw new ResourceNotFoundException("The language " + locale.getDisplayLanguage() + " not exist, please upload teh translation!");
        }
        return mapper.mapProductDetailEntitiesToProductDetails(productDetailEntities);
    }
}
