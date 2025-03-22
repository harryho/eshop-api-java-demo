package com.eshop.api.products.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@SpringBootTest
@ContextConfiguration
@WithMockUser(username = "harry.ho@test.com", roles = { "ADMIN" })
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

    static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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
                .andExpect(jsonPath("$.releaseDate", is("2022-01-01")))
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

    @Test
    @DisplayName("PUT /products/1 - 204 No Content")
    void testPutProductSuccess() throws Exception {

        Product putProduct = new Product(Long.valueOf(1),
                productName + " UPDATED", productGenre + " UPDATED",
                BigDecimal.valueOf(222),
                Integer.valueOf(22),
                releaseDate, imageUri + " UPDATED");
        // Setup our mocked service

        doReturn(putProduct).when(service).saveProduct(any());
        doReturn(Optional.of(mockProduct)).when(service).getProductById(1L);
        // Execute the PUT request
        mockMvc.perform(put("/products/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                // .header(HttpHeaders.IF_MATCH, 7)
                .content(asJsonString(putProduct)))
                // Validate the response code and content type
                .andExpect(status().isNoContent());

        // Execute the GET request
        mockMvc.perform(get("/products/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(productName + " UPDATED")))
                .andExpect(jsonPath("$.genre", is(productGenre + " UPDATED")))
                .andExpect(jsonPath("$.unitPrice", is(222)))
                .andExpect(jsonPath("$.unitInStock", is(22)));
                // .andExpect(jsonPath("$.releaseDate", is("2022-01-01")))
                // .andExpect(jsonPath("$.imageUri", is(imageUri + " UPDATED")));
    }

    @Test
    @DisplayName("POST /products/1 - 204 No Content")
    void testPostProductSuccess() throws Exception {

        Product postProduct = new Product(Long.valueOf(1),
                productName + " CREATED", productGenre + " CREATED",
                BigDecimal.valueOf(222),
                Integer.valueOf(22),
                releaseDate, imageUri + " CREATED");
        // Setup our mocked service
        doReturn(postProduct).when(service).saveProduct(any());
        // Execute the POST request
        mockMvc.perform(post("/products", 1)
                .contentType(MediaType.APPLICATION_JSON)
                // .header(HttpHeaders.IF_MATCH, 7)
                .content(asJsonString(postProduct)))
                // Validate the response code and content type
                .andExpect(status().isOk())

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(productName + " CREATED")))
                .andExpect(jsonPath("$.genre", is(productGenre + " CREATED")))
                .andExpect(jsonPath("$.unitPrice", is(222)))
                .andExpect(jsonPath("$.unitInStock", is(22)))
        .andExpect(jsonPath("$.releaseDate", is("2022-01-01")))
        .andExpect(jsonPath("$.imageUri", is(imageUri+ " CREATED")));
    }

}
