package com.bezro.shopRESTfulAPI.controllers;

import com.bezro.shopRESTfulAPI.entities.Product;
import com.bezro.shopRESTfulAPI.services.OrderService;
import com.bezro.shopRESTfulAPI.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @InjectMocks
    private OrderController orderController;

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
    @WithMockUser("userTest")
    @jakarta.transaction.Transactional
    @DisplayName("Test get order response when create order success")
    void shouldReturnOrderResponse_WhenCreateOrder() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("3"))
                .andExpect(jsonPath("$.userId").value("2"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.totalPrice").value(35.0))
                .andExpect(jsonPath("$.status").value("PROCESSING"))
                .andExpect(jsonPath("$.orderItems").exists())
                .andExpect(jsonPath("$.orderItems").isArray())
                .andExpect(jsonPath("$.orderItems[0].id").value("3"))
                .andExpect(jsonPath("$.orderItems[0].price").value(5.0))
                .andExpect(jsonPath("$.orderItems[0].quantity").value(7.0))
                .andExpect(jsonPath("$.orderItems[0].product").exists())
                .andExpect(jsonPath("$.orderItems[0].product.id").value("2"))
                .andExpect(jsonPath("$.orderItems[0].product.price").value(5.0))
                .andExpect(jsonPath("$.orderItems[0].product.quantity").value(0.0))
                .andExpect(jsonPath("$.orderItems[0].product.name").value("Product 2"))
                .andExpect(jsonPath("$.orderItems[0].product.description").value("Description 2"));
    }

    @Test
    @Sql(scripts = "classpath:db/populateDB.sql")
    @Sql(scripts = "classpath:db/dropDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser("userTestWithEmptyCart")
    @jakarta.transaction.Transactional
    @DisplayName("Test get 400 when create order with empty cart")
    void shouldGetBadRequest_WhenCreateOrderWithEmptyCart() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{}"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @Sql(scripts = "classpath:db/populateDB.sql")
    @Sql(scripts = "classpath:db/dropDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser("userTest")
    @jakarta.transaction.Transactional
    @DisplayName("Test get 400 when create order with cart items with insufficient product stock")
    void shouldGetBadRequest_WhenCreateOrderAndProductStockIsNotSufficient() throws Exception {
        // Arrange
        Product product = productService.findById(2L);
        product.setQuantity(2.0);

        // Act
        // Assert
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{}"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.messages").exists())
                .andExpect(jsonPath("$.messages").isArray())
                .andExpect(jsonPath("$.messages[0]").value("Not enough product with id: 2"));
    }

    //TODO: fix 401/403 issue and remove @Disabled
    @Test
    @Disabled
    void shouldGetUnauthorized_WhenCreateOrderByUnauthorizedUser() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("User should be authenticated"));
    }
}