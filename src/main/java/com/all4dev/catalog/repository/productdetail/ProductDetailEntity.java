package com.all4dev.catalog.repository.productdetail;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Date;
import java.util.Locale;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name="product_i18n")
@Data
class ProductDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_i18n_seq_generator")
    @SequenceGenerator(name = "product_i18n_seq_generator", sequenceName = "product_i18n_seq")
    private Long id;

    @NotNull
    private Long productId;

    private String name;

    private String description;

    @NotNull
    private Locale locale;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private long createdAt;

    @Column(name = "modified_at")
    @LastModifiedDate
    private long modifiedAt;
}
