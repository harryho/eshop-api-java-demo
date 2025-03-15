package com.eshop.api.products.web;

import static org.mockito.Mockito.doReturn;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import com.eshop.api.products.domain.Product;
import com.eshop.api.products.service.ProductService;

@SpringBootTest
@ContextConfiguration
@WithMockUser(username = "harry.ho@test.com", roles = {"ADMIN"})
@WebAppConfiguration
@AutoConfigureMockMvc
public class ProductControllerTest {


    @MockBean
    private ProductService service;

    @Autowired
    private MockMvc mockMvc;

    final String productName = "PRODUCT NAME";
    final String productGenre = "PRODUCT GENRE";
    final String imageUri = "IMAGE URI";
    final LocalDate releaseDate = LocalDate.of(2022, 1, 1);

    final Product mockProduct = new Product(Long.valueOf(1),
            productName, productGenre,
            BigDecimal.valueOf(22),
            Integer.valueOf(2),
            releaseDate, imageUri);

    @Test
    @DisplayName("GET /products/1 - 200 Success")
    void testGetProductByIdFound() throws Exception {
        // Setup our mocked service
        doReturn(Optional.of(mockProduct)).when(service).getProductById(1L);

        // Execute the GET request
        mockMvc.perform(get("/products/{id}", 1))

                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(productName)))
                .andExpect(jsonPath("$.genre", is(productGenre)))
                .andExpect(jsonPath("$.unitPrice", is(22)))
                .andExpect(jsonPath("$.unitInStock", is(2)))
                .andExpect(jsonPath("$.imageUri", is(imageUri)));
    }

    @Test
    @DisplayName("GET /product/1 - 404 Not found")
    void testGetProductByIdNotFound() throws Exception {
        // Setup our mocked service
        doReturn(Optional.empty()).when(service).getProductById(1L);

        // Execute the GET request
        mockMvc.perform(get("/products/{id}", 1))

                // Validate that we get a 404 Not Found response
                .andExpect(status().isNotFound());
    }

}
