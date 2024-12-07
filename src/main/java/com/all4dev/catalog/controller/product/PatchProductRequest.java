package com.all4dev.catalog.controller.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.Locale;

@Data
class PatchProductRequest {

    private BigDecimal price;

    private int taxPercent;

    private String name;

    private String description;

    private Locale locale;
}
