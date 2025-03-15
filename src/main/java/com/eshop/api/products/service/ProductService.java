package com.eshop.api.products.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.eshop.api.products.domain.Product;
import com.eshop.api.products.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(
            ProductRepository productRepository
    ) {
        this.productRepository = productRepository;
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public void removeProductById(Long id) {
        productRepository.deleteById(id);
    }

}
