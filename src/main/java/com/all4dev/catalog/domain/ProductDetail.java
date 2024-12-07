package com.all4dev.catalog.domain;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Locale;

@Data
public class ProductDetail implements Serializable {

    private Long productId;

    private String name;

    private String description;

    private Locale locale;

}
