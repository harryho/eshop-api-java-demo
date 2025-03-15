package com.eshop.api.products.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.*;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @NotNull
    private String name;

    private String genre;
    private BigDecimal unitPrice;
    private Integer unitInStock;
    private LocalDate releaseDate;
    private String ImageUri;
}
