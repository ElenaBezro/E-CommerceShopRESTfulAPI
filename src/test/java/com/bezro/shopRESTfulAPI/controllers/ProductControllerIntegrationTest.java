package com.bezro.shopRESTfulAPI.controllers;

import com.bezro.shopRESTfulAPI.entities.Product;
import com.bezro.shopRESTfulAPI.repositories.ProductRepository;
import com.bezro.shopRESTfulAPI.services.ProductService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class ProductControllerIntegrationTest {
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @InjectMocks
    private ProductController productController;

    @Autowired
    //to fake http requests
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @Sql(scripts = "classpath:db/populateDB.sql")
    @Sql(scripts = "classpath:db/dropDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails("adminTest")
    @jakarta.transaction.Transactional
    @DisplayName("Test get product when add product success")
    void shouldReturnProduct_WhenAddProduct() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"name\":\"New Product\"," +
                                " \"description\":\"New Product Description\"," +
                                " \"price\":5.5," +
                                " \"quantity\":100.5}"
                        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("4"))
                .andExpect(jsonPath("$.name").value("New Product"))
                .andExpect(jsonPath("$.description").value("New Product Description"))
                .andExpect(jsonPath("$.price").value(5.5))
                .andExpect(jsonPath("$.quantity").value(100.5));
    }

    @Test
    @Sql(scripts = "classpath:db/populateDB.sql")
    @Sql(scripts = "classpath:db/dropDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails("adminTest")
    @jakarta.transaction.Transactional
    @DisplayName("Test get 400 when add product without required payload fields")
    void shouldThrowBadRequest_WhenAddProductWithoutPayload() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages").isArray())
                .andExpect(jsonPath("$.messages.size()").value(3))
                .andExpect(jsonPath("$.messages", containsInAnyOrder(
                        "The name is required.",
                        "Price must be greater than zero",
                        "Stock quantity must be greater than zero")));
    }

    @Test
    @DisplayName("Test for 401 response when adding a product by an unauthenticated user")
    void shouldGet401StatusCode_WhenAddProductByUnauthenticatedUser() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"name\":\"New Product\"," +
                                " \"description\":\"New Product Description\"," +
                                " \"price\":5.5," +
                                " \"quantity\":100.5}"
                        ))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Sql(scripts = "classpath:db/populateDB.sql")
    @Sql(scripts = "classpath:db/dropDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails("userTest")
    @jakarta.transaction.Transactional
    @DisplayName("Test for 403 response when adding a product by an unauthorized user")
    void shouldGet403StatusCode_WhenAddProductUnauthorizedUser() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"name\":\"New Product\"," +
                                " \"description\":\"New Product Description\"," +
                                " \"price\":5.5," +
                                " \"quantity\":100.5}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @Sql(scripts = "classpath:db/populateDB.sql")
    @Sql(scripts = "classpath:db/dropDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @jakarta.transaction.Transactional
    @DisplayName("Test get all products with pagination when get all products success")
    void shouldReturnListOfProducts_WhenGetAllProductsPaginated() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(get("/api/v1/products?pageNumber=1&pageSize=2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(3))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.products").isArray())
                .andExpect(jsonPath("$.products[0].id").value("3"))
                .andExpect(jsonPath("$.products[0].name").value("A-Product 3"))
                .andExpect(jsonPath("$.products[0].description").value("Description 3"))
                .andExpect(jsonPath("$.products[0].price").value(55.0))
                .andExpect(jsonPath("$.products[0].quantity").value(77.0));
    }

    @Test
    @Sql(scripts = "classpath:db/populateDB.sql")
    @Sql(scripts = "classpath:db/dropDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @jakarta.transaction.Transactional
    @DisplayName("Test get 204 when retrieving all products from an empty page")
    void shouldGet204StatusCode_WhenGetAllProductsFromEmptyPage() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(get("/api/v1/products?pageNumber=10&pageSize=20")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{}"))
                .andExpect(status().isNoContent());
    }

    @Test
    @Sql(scripts = "classpath:db/populateDB.sql")
    @Sql(scripts = "classpath:db/dropDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @jakarta.transaction.Transactional
    @DisplayName("Test get all products with pagination when using default request parameters")
    void shouldReturnListOfProducts_WhenGetAllProductsPaginatedDefaultRequestParameters() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(get("/api/v1/products?pageNumber&pageSize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(3))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.products").isArray())
                .andExpect(jsonPath("$.products.size()").value(3));
    }

    @Test
    @Sql(scripts = "classpath:db/populateDB.sql")
    @Sql(scripts = "classpath:db/dropDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @jakarta.transaction.Transactional
    @DisplayName("Test get all products sorted with pagination when get all products success")
    void shouldReturnListOfProducts_WhenGetAllProductsPaginatedSorted() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(get("/api/v1/products?pageNumber=1&pageSize=2&sort=name")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(3))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.products").isArray())
                .andExpect(jsonPath("$.products[0].id").value("2"))
                .andExpect(jsonPath("$.products[0].name").value("Product 2"))
                .andExpect(jsonPath("$.products[0].description").value("Description 2"))
                .andExpect(jsonPath("$.products[0].price").value(5.0))
                .andExpect(jsonPath("$.products[0].quantity").value(7.0));
    }

    @Test
    @Sql(scripts = "classpath:db/populateDB.sql")
    @Sql(scripts = "classpath:db/dropDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @jakarta.transaction.Transactional
    @DisplayName("Test get 204 when retrieving all sorted products from an empty page")
    void shouldGet204StatusCode_WhenGetAllProductsFromEmptyPageSorted() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(get("/api/v1/products?pageNumber=10&pageSize=20&sort=name")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{}"))
                .andExpect(status().isNoContent());
    }

    @Test
    @Sql(scripts = "classpath:db/populateDB.sql")
    @Sql(scripts = "classpath:db/dropDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @jakarta.transaction.Transactional
    @DisplayName("Test get all products sorted with pagination when using default request parameters")
    void shouldReturnListOfProducts_WhenGetAllProductsPaginatedDefaultRequestParametersSorted() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(get("/api/v1/products?pageNumber=1&pageSize=2&sort")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(3))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.products").isArray())
                .andExpect(jsonPath("$.products[0].id").value("2"))
                .andExpect(jsonPath("$.products[0].name").value("Product 2"))
                .andExpect(jsonPath("$.products[0].description").value("Description 2"))
                .andExpect(jsonPath("$.products[0].price").value(5.0))
                .andExpect(jsonPath("$.products[0].quantity").value(7.0));
    }

    @Test
    @Sql(scripts = "classpath:db/populateDB.sql")
    @Sql(scripts = "classpath:db/dropDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails("admin")
    @jakarta.transaction.Transactional
    @DisplayName("Test get product when update product by authorized user success")
    void shouldReturnProduct_WhenUpdateProduct() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(put("/api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"name\":\"New Product Name\"," +
                                " \"description\":\"New Product Description\"," +
                                " \"price\": 100.0," +
                                " \"quantity\": 200.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("New Product Name"))
                .andExpect(jsonPath("$.description").value("New Product Description"))
                .andExpect(jsonPath("$.price").value(100.0))
                .andExpect(jsonPath("$.quantity").value(200.0));
    }

    @Test
    @Sql(scripts = "classpath:db/populateDB.sql")
    @Sql(scripts = "classpath:db/dropDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails("userTest")
    @jakarta.transaction.Transactional
    @DisplayName("Test for 403 response when updating a product by an unauthorized user")
    void shouldGet403StatusCode_WhenUpdateProductUnauthorizedUser() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(put("/api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"name\":\"New Product Name\"," +
                                " \"description\":\"New Product Description\"," +
                                " \"price\": 100.0," +
                                " \"quantity\": 200.0}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @Sql(scripts = "classpath:db/populateDB.sql")
    @Sql(scripts = "classpath:db/dropDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails("admin")
    @jakarta.transaction.Transactional
    @DisplayName("Test get 400 when update product with non-existing product id")
    void shouldThrowBadRequest_WhenUpdateProductWithNonExistingId() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(put("/api/v1/products/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"name\":\"New Product Name\"," +
                                " \"description\":\"New Product Description\"," +
                                " \"price\": 100.0," +
                                " \"quantity\": 200.0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Product with id: 100 does not exist"));
    }

    @Test
    @Sql(scripts = "classpath:db/populateDB.sql")
    @Sql(scripts = "classpath:db/dropDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails("admin")
    @jakarta.transaction.Transactional
    @DisplayName("Test get 400 when update product with invalid payload")
    void shouldThrowBadRequest_WhenUpdateProductWithInvalidPayload() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(put("/api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages", containsInAnyOrder(
                        "The name is required.",
                        "Price must be greater than zero",
                        "Stock quantity must be greater than zero"
                )));
    }

    @Test
    @DisplayName("Test for 401 response when updating a product by an unauthenticated user")
    void shouldGet401StatusCode_WhenUpdateProductByUnauthenticatedUser() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(put("/api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"name\":\"New Product Name\"," +
                                " \"description\":\"New Product Description\"," +
                                " \"price\": 100.0," +
                                " \"quantity\": 200.0}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Sql(scripts = "classpath:db/populateDB.sql")
    @Sql(scripts = "classpath:db/dropDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails("admin")
    @jakarta.transaction.Transactional
    @DisplayName("Test get 200 status when delete product by authorized user success")
    void shouldReturn200StatusCode_WhenDeleteProduct() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(delete("/api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{}"))
                .andExpect(status().isOk());
        //TODO: Do I need this check? same in update method
        Optional<Product> deletedProduct = productRepository.findById(1L);
        assertTrue(deletedProduct.isEmpty(), "Product should be deleted");
    }

    @Test
    @Sql(scripts = "classpath:db/populateDB.sql")
    @Sql(scripts = "classpath:db/dropDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails("userTest")
    @jakarta.transaction.Transactional
    @DisplayName("Test for 403 response when deleting a product by an unauthorized user")
    void shouldGet403StatusCode_WhenDeleteProductUnauthorizedUser() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(delete("/api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @Sql(scripts = "classpath:db/populateDB.sql")
    @Sql(scripts = "classpath:db/dropDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails("admin")
    @jakarta.transaction.Transactional
    @DisplayName("Test get 400 when delete product with non-existing product id")
    void shouldThrowBadRequest_WhenDeleteProductWithNonExistingId() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(delete("/api/v1/products/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Product with id: 100 does not exist"));
    }

    @Test
    @DisplayName("Test for 401 response when deleting a product by an unauthenticated user")
    void shouldGet401StatusCode_WhenDeleteProductByUnauthenticatedUser() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(delete("/api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }
}