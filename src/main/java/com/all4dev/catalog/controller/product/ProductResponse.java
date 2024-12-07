package com.all4dev.catalog.controller.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.util.Locale;

@Data
class ProductResponse extends RepresentationModel<ProductResponse> {

    private Long id;

    private BigDecimal price;

    private int taxPercent;

    private String name;

    private String description;

    private Locale locale;
}
