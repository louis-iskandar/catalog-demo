package com.all4dev.catalog.domain;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class Product implements Serializable {

    private Long id;

    private BigDecimal price;

    private int taxPercent;
}
