package com.all4dev.catalog.service;

import com.all4dev.catalog.domain.Product;
import com.all4dev.catalog.domain.ProductDetail;
import com.all4dev.catalog.exception.ResourceAlreadyExistException;
import com.all4dev.catalog.exception.ResourceNotFoundException;
import com.all4dev.catalog.repository.product.ProductRepository;
import com.all4dev.catalog.repository.productdetail.ProductDetailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Locale;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);


    final ProductRepository productRepository;
    final ProductDetailRepository productDetailRepository;

    public ProductServiceImpl(ProductRepository productRepository, ProductDetailRepository productDetailRepository) {
        this.productRepository = productRepository;
        this.productDetailRepository = productDetailRepository;
    }

    @Override
    public boolean isProductExist(Long productId) {
        return productRepository.isProductExist(productId);
    }

    @Override
    public void createProduct(Product product, ProductDetail productDetail) throws ResourceAlreadyExistException {
        if (productDetail.getLocale() == null) {
            productDetail.setLocale(DEFAULT_LOCALE);
        }
        if (productRepository.isProductExist(product.getId())) {
            String message = "product with ID: " + product.getId() + " already exist!";
            LOGGER.error(message);
            throw new ResourceAlreadyExistException(message);
        }
        productRepository.createAndUpdateProduct(product);
        productDetailRepository.createProductDetail(productDetail);
    }

    @Override
    public Product updateProduct(Product product) throws ResourceNotFoundException, InvocationTargetException, IllegalAccessException, ResourceAlreadyExistException {
        if (!productRepository.isProductExist(product.getId())) {
            String message = "product with ID: " + product.getId() + " not found!";
            LOGGER.error(message);
            throw new ResourceNotFoundException(message);
        }
        return productRepository.updateProduct(product);
    }

    @Override
    public ProductDetail updateProductDetail(ProductDetail productDetail) throws ResourceNotFoundException, InvocationTargetException, IllegalAccessException {
        if (!productRepository.isProductExist(productDetail.getProductId())) {
            String message = "product with ID: " + productDetail.getProductId() + " not found!";
            LOGGER.error(message);
            throw new ResourceNotFoundException(message);
        }
        if (productDetail.getLocale() == null) {
            productDetail.setLocale(DEFAULT_LOCALE);
        }
        ProductDetail updated;
        if (productDetailRepository.isProductDetailExist(productDetail.getProductId(), productDetail.getLocale())) {
             updated = productDetailRepository.udateProductDetail(productDetail);
        } else {
            updated = productDetailRepository.createProductDetail(productDetail);
        }
        return updated;
    }

    @Override
    public List<Product> getProducts() {
        return productRepository.getAllProducts();
    }

    @Override
    public List<ProductDetail> getProductDetails(Locale locale) throws ResourceNotFoundException {
        return productDetailRepository.getAllProductDetails(locale);
    }

    @Override
    public Product getProductById(Long productId) throws ResourceNotFoundException {
        return productRepository.getProduct(productId);
    }

    @Override
    public ProductDetail getProductDetailById(Long productId, Locale locale) throws ResourceNotFoundException {
        return productDetailRepository.getProductDetail(productId, locale);
    }
}
