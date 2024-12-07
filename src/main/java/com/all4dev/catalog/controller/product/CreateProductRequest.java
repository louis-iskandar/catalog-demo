package com.all4dev.catalog.controller.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Locale;

@Data
class CreateProductRequest {

    @NotNull(message = "id can't be null")
    private Long id;

    @Min(value = 1, message = "Price should be greater than 1")
    private BigDecimal price;

    @Min(value = 1, message = "Tax percent should be greater than 1")
    private int taxPercent;

    @NotBlank(message = "Name cannot be null or blank")
    private String name;

    private String description;

    private Locale locale;
}
