package com.all4dev.catalog.repository.product;

import org.springframework.data.repository.CrudRepository;

interface ProductJPARepository extends CrudRepository<ProductEntity, Long> {
}
