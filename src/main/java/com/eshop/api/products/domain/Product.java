package com.eshop.api.products.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;
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
    @Column(name="unit_price")
    private BigDecimal unitPrice;
    @Column(name="unit_in_stock")
    private Integer unitInStock;
    @Column(name="release_date")
    private LocalDate releaseDate;
    @Column(name="image_uri")
    private String ImageUri;
}
