package com.all4dev.catalog.controller.product;

import com.all4dev.catalog.domain.Product;
import com.all4dev.catalog.domain.ProductDetail;
import com.all4dev.catalog.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@CrossOrigin(maxAge = 3600)
@RestController
public class ProductController {
    final ProductService service;

    final ProductControllerMapper mapper;

    public ProductController(ProductService service, ProductControllerMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping("/products")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createNewProduct(@Valid @RequestBody CreateProductRequest request) throws Exception {
        service.createProduct(mapper.mapCreateProductRequestToProduct(request), mapper.mapCreateProductRequestToProductDetail(request));
        Product product = service.getProductById(request.getId());
        Locale locale = request.getLocale();
        if (locale == null) {
            locale = ProductService.DEFAULT_LOCALE;
        }
        ProductDetail productDetail = service.getProductDetailById(request.getId(), locale);
        return mappingToResponse(product, productDetail);
    }

    @GetMapping("/products")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getProducts(@RequestParam("locale") Locale locale) throws Exception {
        List<Product> products = service.getProducts();
        List<ProductResponse> entries = Collections.emptyList();
        if (!products.isEmpty()) {
            entries = mapper.mapProductListToProductResponseList(products);
            List<ProductDetail> productDetails = service.getProductDetails(locale);
            entries.forEach(response -> {
                ProductDetail detail = productDetails.stream()
                        .filter(item -> response.getId().equals(item.getProductId()))
                        .findAny()
                        .orElse(null);
                if (detail != null) {
                    response.setName(detail.getName());
                    response.setDescription(detail.getDescription());
                }
                Link selfLink = linkTo(ProductController.class).slash("products").slash(response.getId()).withSelfRel();
                response.add(selfLink);
            });
        }
        return entries;
    }

    @GetMapping("/products/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ProductResponse> getProductById(@PathVariable String id) throws Exception {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @PatchMapping("/products/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponse updateProduct(@NotNull @PathVariable Long id, @Valid @RequestBody PatchProductRequest request) throws Exception {
        Product product = service.updateProduct(mapper.mapPatchProductRequestToProduct(id, request));
        ProductDetail productDetail = service.updateProductDetail(mapper.mapPatchProductRequestToProductDetail(id, request));
        return mappingToResponse(product, productDetail);
    }

    private ProductResponse mappingToResponse(Product product, ProductDetail productDetail) {
        ProductResponse productResponse = mapper.mapCreateProductAndDetailToProductResponse(product, productDetail);
        Link selfLink = linkTo(ProductController.class).slash(product.getId()).withSelfRel();
        productResponse.add(selfLink);
        return productResponse;
    }
}
