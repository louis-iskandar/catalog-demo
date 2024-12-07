package com.all4dev.catalog.controller.file;

import com.opencsv.bean.CsvBindByPosition;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductItem {

    //@CsvBindByName(column = "Produktnummer", required = true)
    @CsvBindByPosition(position = 0)
    @NotNull(message = "id can't be null")
    private Long id;

    //@CsvBindByName(column = "Preis", required = true)
    @CsvBindByPosition(position = 2)
    @Min(value = 1, message = "Price should be greater than 1")
    private String price;

    //@CsvBindByName(column = "Mwst_Prozent", required = true)
    @CsvBindByPosition(position = 3)
    @Min(value = 1, message = "Tax percent should be greater than 1")
    private float taxPercent;

    //@CsvBindByName(column = "Produktname", required = true)
    @CsvBindByPosition(position = 1)
    @NotBlank(message = "Name cannot be null or blank")
    private String name;

    //@CsvBindByName(column = "Beschreibung", required = false)
    @CsvBindByPosition(position = 4)
    private String description;
}
