package com.eshop.api.products.respository;

import com.eshop.api.products.domain.Product;
import com.eshop.api.products.repository.ProductRepository;
import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.DBUnitExtension;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ExtendWith({ DBUnitExtension.class, SpringExtension.class })
@SpringBootTest
@ActiveProfiles("test")
public class ProductRepositoryTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ProductRepository repository;

    public ConnectionHolder getConnectionHolder() {
        // Return a function that retrieves a connection from our data source
        return () -> dataSource.getConnection();
    }

    final String productName = "PRODUCT NAME";
    final String productGenre = "PRODUCT GENRE";
    final String imageUri = "IMAGE URI";
    final LocalDate releaseDate = LocalDate.of(2022, 1, 1);

    // @Test
    // @DataSet("products.yml")
    // void testFindAllSucces() {
    // List<Product> products = repository.findAll();
    // Assertions.assertEquals(2, products.size(), "We should have 2 products in our
    // database");
    // }

    @Test
    @DataSet("products.yml")
    void testFindByIdSuccess() {
        // Find the product with ID 200
        Optional<Product> product = repository.findById(1L);
        // Validate that we found it
        Assertions.assertTrue(product.isPresent(), "Product with ID 2 should be found");

        // Validate the product values
        Product p = product.get();
        Assertions.assertEquals(1, p.getId().intValue(), "Product ID should be 1");
        Assertions.assertEquals("PRODUCT NAME 1", p.getName(), "Product name should be \"PRODUCT NAME 1\"");
        Assertions.assertEquals("PRODUCT GENRE 1", p.getGenre(), "Product genre should be \"PRODUCT GENRE 1\"");
        Assertions.assertEquals(11, p.getUnitPrice().intValue(), "Product price should be 11");
        Assertions.assertEquals(1, p.getUnitInStock().intValue(), "Product in stock should be 1");
        Assertions.assertEquals("IMAGE URI 1", p.getImageUri(), "Product image uri should be \"IMAGE URI 1\"");
    }

    @Test
    @DataSet("products.yml")
    void testFindAllSuccess() {
        // Find all products
        List<Product> products = repository.findAll();
        // Validate that we found 2 products
        Assertions.assertTrue(products.size() == 2., "Product list should have 2 records");

        // Validate the 2nd product
        Product p = products.get(1);
        Assertions.assertEquals(2, p.getId().intValue(), "Product ID should be 2");
        Assertions.assertEquals("PRODUCT NAME 2", p.getName(), "Product name should be \"PRODUCT NAME 2\"");
        Assertions.assertEquals("PRODUCT GENRE 2", p.getGenre(), "Product genre should be \"PRODUCT GENRE 2\"");
        Assertions.assertEquals(22, p.getUnitPrice().intValue(), "Product price should be 22");
        Assertions.assertEquals(2, p.getUnitInStock().intValue(), "Product in stock should be 2");
        Assertions.assertEquals("IMAGE URI 2", p.getImageUri(), "Product image uri should be \"IMAGE URI 2\"");
    }

    @Test
    @DataSet("products.yml")
    void testUpdateSuccess() {
        // Update product 200's name, quantity, and version
        Product product = repository.findById(2L).get();
        product.setName("PRODUCT NAME 22");
        product.setGenre("PRODUCT GENRE 22");
        product.setUnitPrice(BigDecimal.valueOf(222));
        product.setUnitInStock(22);
        Product updatedResult = repository.save(product);

        // Validate that our product is returned by update()
        Assertions.assertTrue(updatedResult.getName() == "PRODUCT NAME 22", "The product should have been updated");

        // Retrieve product 200 from the database and validate its fields
        Optional<Product> loadedProduct = repository.findById(2L);
        Assertions.assertTrue(loadedProduct.isPresent(), "Updated product should exist in the database");
        Assertions.assertEquals("PRODUCT NAME 22", loadedProduct.get().getName(),
                "Product name should be \"PRODUCT NAME 22\"");
        Assertions.assertEquals("PRODUCT GENRE 22", loadedProduct.get().getGenre(),
                "Product genre should be \"PRODUCT GENRE 22\"");
        Assertions.assertEquals(222, loadedProduct.get().getUnitPrice().intValue(), "Product price should be 222");
        Assertions.assertEquals(22, loadedProduct.get().getUnitInStock().intValue(), "Product in stock should be 22");

    }

    @Test
    @DataSet("products.yml")
    void testUpdateFailure() {
        // Update product 300's name, quantity, and version
        Product product = new Product(Long.valueOf(3),
                productName, productGenre,
                BigDecimal.valueOf(22),
                Integer.valueOf(2),
                releaseDate, imageUri);
        Product updatedResult = null;
        try {
            updatedResult = repository.save(product);
        } catch (Exception e) {
            // TODO: handle exception
        }

        // Validate that our product is returned by update()
        Assertions.assertNull(updatedResult, "The product should not have been updated");
    }

    @Test
    @DataSet("products.yml")
    void testFindByIdNotFound() {
        // Find the product with ID 300
        Optional<Product> product = repository.findById(100L);

        // Validate that we found it
        Assertions.assertFalse(product.isPresent(), "Product with ID 100 should be not be found");
    }
}
