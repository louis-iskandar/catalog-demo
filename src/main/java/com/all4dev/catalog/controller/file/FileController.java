package com.all4dev.catalog.controller.file;

import com.all4dev.catalog.domain.Product;
import com.all4dev.catalog.domain.ProductDetail;
import com.all4dev.catalog.exception.CSVImportExceptionException;
import com.all4dev.catalog.exception.InvalidFileFormatException;
import com.all4dev.catalog.exception.InvalidFileNameException;
import com.all4dev.catalog.service.ProductService;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
@CrossOrigin(maxAge = 3600)
@RestController
@Validated
public class FileController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileController.class);

    final static String TYPE = "text/csv";

    final ProductService service;

    final FileControllerMapper mapper;

    public FileController(ProductService service, FileControllerMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping("/files")
    public ResponseEntity<?> createNewProduct(@RequestParam("file") MultipartFile file) throws Exception {
        if (!hasCsvFormat(file)) {
            String message = "File extension " + file.getContentType() + " is invalid!";
            LOGGER.error(message);
            throw new InvalidFileFormatException(message);
        }
        final Locale locale;
        String[] fileNameSplit = file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf('.')).split("_");
        if (fileNameSplit.length > 1) {
            String localeString = fileNameSplit[1];
            String[] localeStringSplit = localeString.split("-");
            if (localeStringSplit.length < 2) {
                throw new InvalidFileNameException("Invalid Locale String in the file name!");
            }
            String language = localeStringSplit[0].toLowerCase();
            String country = localeStringSplit[1].toUpperCase();
            locale = new Locale(language, country);
        } else {
            locale = ProductService.DEFAULT_LOCALE;
        }
        Reader reader = new InputStreamReader(file.getInputStream());
        HeaderColumnNameMappingStrategy<ProductItem> strategy = new HeaderColumnNameMappingStrategy<>();
        strategy.setType(ProductItem.class);
        CsvToBean<ProductItem> builder = new CsvToBeanBuilder<ProductItem>(reader)
                .withSkipLines(1)
                .withType(ProductItem.class)
                .withFieldAsNull(CSVReaderNullFieldIndicator.BOTH)
                .withIgnoreEmptyLine(true)
                .withIgnoreLeadingWhiteSpace(true)
                .withSeparator(';')
                .build();
        List<ProductItem> productItems = builder.parse();
        AtomicInteger lineCount = new AtomicInteger(1);
        Map<Integer, Exception> exceptions = new HashMap<>();
        productItems.forEach(element -> {
            Product product = mapper.mapProductItemToProduct(element);
            ProductDetail productDetail = mapper.mapProductItemToProductDetail(element, locale);
            try {
                if (!service.isProductExist(product.getId())) {
                    service.createProduct(product, productDetail);
                } else {
                    service.updateProduct(product);
                    service.updateProductDetail(productDetail);
                }
            } catch (Exception exception) {
                exceptions.put(lineCount.get(), exception);
                LOGGER.error(exception.getMessage(), exception);
            }
            lineCount.getAndIncrement();
        });
        if (!exceptions.isEmpty()) {
            throw new CSVImportExceptionException(exceptions);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private boolean hasCsvFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }
}
