package com.all4dev.catalog.repository.product;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.sql.Date;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "product")
@Data
class ProductEntity {

    @Id
    private Long id;

    private BigDecimal price;

    private int taxPercent;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private long createdAt;

    @Column(name = "modified_at")
    @LastModifiedDate
    private long modifiedAt;
}
