package com.all4dev.catalog.service;

import com.all4dev.catalog.domain.Product;
import com.all4dev.catalog.domain.ProductDetail;
import com.all4dev.catalog.exception.ResourceAlreadyExistException;
import com.all4dev.catalog.exception.ResourceNotFoundException;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Locale;

public interface ProductService {

    Locale DEFAULT_LOCALE = Locale.GERMANY;

    boolean isProductExist(Long productId);

    void createProduct(Product product, ProductDetail productDetail) throws ResourceAlreadyExistException;

    Product updateProduct(Product product) throws ResourceNotFoundException, InvocationTargetException, IllegalAccessException, ResourceAlreadyExistException;

    ProductDetail updateProductDetail(ProductDetail productDetail) throws ResourceNotFoundException, InvocationTargetException, IllegalAccessException;

    List<Product> getProducts();

    List<ProductDetail> getProductDetails(Locale locale) throws ResourceNotFoundException;

    Product getProductById(Long productId) throws ResourceNotFoundException;

    ProductDetail getProductDetailById(Long productId, Locale locale) throws ResourceNotFoundException;

}
