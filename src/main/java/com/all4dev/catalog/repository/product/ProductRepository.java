package com.all4dev.catalog.repository.product;

import com.all4dev.catalog.domain.Product;
import com.all4dev.catalog.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository
public class ProductRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductRepository.class);

    final ProductEntityMapper mapper;

    final ProductJPARepository jpaRepository;

    public ProductRepository(ProductEntityMapper mapper, ProductJPARepository jpaRepository) {
        this.mapper = mapper;
        this.jpaRepository = jpaRepository;
    }

    public void createAndUpdateProduct(Product product) {
        ProductEntity productEntity = mapper.mapProductToProductEntity(product);
        jpaRepository.save(productEntity);
    }

    public boolean isProductExist(Long productId) {
        Optional<ProductEntity> productEntity = jpaRepository.findById(productId);
        return productEntity.isPresent();
    }

    public Product getProduct(Long productId) throws ResourceNotFoundException {
        Optional<ProductEntity> optional = jpaRepository.findById(productId);
        if (optional.isEmpty()) {
            String message = "product with ID: " + productId + " not found!";
            LOGGER.error(message);
            throw new ResourceNotFoundException(message);
        }
        ProductEntity productEntity = optional.get();
        return mapper.mapProductEntityToProduct(productEntity);
    }

    public Product updateProduct(Product product) throws ResourceNotFoundException {
        Optional<ProductEntity> optional = jpaRepository.findById(product.getId());
        if (optional.isEmpty()) {
            String message = "product with ID: " + product.getId() + " not found!";
            LOGGER.error(message);
            throw new ResourceNotFoundException(message);
        }
        ProductEntity entity = optional.get();
        ProductEntity update = mapper.mapProductToProductEntity(product);
        BeanUtils.copyProperties(update, entity, getNullPropertyNames(update));
        ProductEntity saveEntity = jpaRepository.save(entity);
        return mapper.mapProductEntityToProduct(saveEntity);
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

    public List<Product> getAllProducts() {
        Iterable<ProductEntity> entities = jpaRepository.findAll();
        List<ProductEntity> result = StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(entities.iterator(), Spliterator.ORDERED), false)
                .toList();
        return mapper.mapProductEntityListToProductList(result);
    }
}
