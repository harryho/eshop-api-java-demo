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
import java.util.List;
import java.util.Optional;

@ExtendWith({DBUnitExtension.class, SpringExtension.class})
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

    @Test
    @DataSet("products.yml")
    void testFindAll() {
        List<Product> products = repository.findAll();
        Assertions.assertEquals(2, products.size(), "We should have 2 products in our database");
    }

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
    void testFindByIdNotFound() {
        // Find the product with ID 300
        Optional<Product> product = repository.findById(100L);

        // Validate that we found it
        Assertions.assertFalse(product.isPresent(), "Product with ID 100 should be not be found");
    }
}

  
