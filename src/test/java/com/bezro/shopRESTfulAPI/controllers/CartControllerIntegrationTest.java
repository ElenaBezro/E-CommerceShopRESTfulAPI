package com.bezro.shopRESTfulAPI.controllers;

import com.bezro.shopRESTfulAPI.entities.CartItem;
import com.bezro.shopRESTfulAPI.repositories.CartRepository;
import com.bezro.shopRESTfulAPI.services.CartService;
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
class CartControllerIntegrationTest {
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @InjectMocks
    private CartController cartController;

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
    @WithUserDetails("userTest")
    @jakarta.transaction.Transactional
    @DisplayName("Test get 400 when add already existing cart item to the cart")
    void shouldGet400StatusCode_WhenAddCartItemThatAlreadyInCart() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(post("/api/v1/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"productId\": 2," +
                                " \"userId\": 2," +
                                " \"quantity\": 5.5}"
                        ))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cart item already exists for the given product and user."));
    }

    @Test
    @Sql(scripts = "classpath:db/populateDB.sql")
    @Sql(scripts = "classpath:db/dropDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails("userTest")
    @jakarta.transaction.Transactional
    @DisplayName("Test get cart item when add a new item to the cart success")
    void shouldReturnCartItem_WhenAddCartItem() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(post("/api/v1/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"productId\": 3," +
                                " \"userId\": 2," +
                                " \"quantity\": 5.5}"
                        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("4"))
                .andExpect(jsonPath("$.product").exists())
                .andExpect(jsonPath("$.product.id").value("3"))
                .andExpect(jsonPath("$.product.name").value("A-Product 3"))
                .andExpect(jsonPath("$.product.description").value("Description 3"))
                .andExpect(jsonPath("$.product.price").value("55.0"))
                .andExpect(jsonPath("$.product.quantity").value("77.0"))
                .andExpect(jsonPath("$.userId").value("2"))
                .andExpect(jsonPath("$.quantity").value(5.5));
    }

    @Test
    @Sql(scripts = "classpath:db/populateDB.sql")
    @Sql(scripts = "classpath:db/dropDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails("adminTest")
    @jakarta.transaction.Transactional
    @DisplayName("Test get 400 when adding a cart item for a user that does not match the logged-in user")
    void shouldThrowBadRequest_WhenAddCartItemAndUserIdDoesNotMatch() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(post("/api/v1/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"productId\": 2," +
                                " \"userId\": 2," +
                                " \"quantity\": 7.0}"
                        ))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User id 2 does not match logged in user id"));
    }

    @Test
    @Sql(scripts = "classpath:db/populateDB.sql")
    @Sql(scripts = "classpath:db/dropDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails("userTest")
    @jakarta.transaction.Transactional
    @DisplayName("Test get 400 when adding a cart item with a non-existing product id")
    void shouldThrowBadRequest_WhenAddCartItemWithInvalidProductId() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(post("/api/v1/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"productId\": 10," +
                                " \"userId\": 2," +
                                " \"quantity\": 7.0}"
                        ))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Product with id: 10 does not exist"));
    }

    @Test
    @Sql(scripts = "classpath:db/populateDB.sql")
    @Sql(scripts = "classpath:db/dropDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails("userTest")
    @jakarta.transaction.Transactional
    @DisplayName("Test get 400 when adding a cart item with a non-existing user id")
    void shouldThrowBadRequest_WhenAddCartItemWithInvalidUserId() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(post("/api/v1/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"productId\": 2," +
                                " \"userId\": 10," +
                                " \"quantity\": 7.0}"
                        ))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User with id: 10 does not exist"));
    }

    @Test
    @Sql(scripts = "classpath:db/populateDB.sql")
    @Sql(scripts = "classpath:db/dropDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails("userTest")
    @jakarta.transaction.Transactional
    @DisplayName("Test get 400 when adding a cart item with insufficient product stock")
    void shouldThrowBadRequest_WhenAddCartItemWithInSufficientProductStock() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(post("/api/v1/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"productId\": 2," +
                                " \"userId\": 2," +
                                " \"quantity\": 100.0}"
                        ))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Product stock is not sufficient"));
    }

    @Test
    @Sql(scripts = "classpath:db/populateDB.sql")
    @Sql(scripts = "classpath:db/dropDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails("userTest")
    @jakarta.transaction.Transactional
    @DisplayName("Test get 400 when add a new item to the cart without required payload fields")
    void shouldThrowBadRequest_WhenAddCartItemWithoutPayload() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(post("/api/v1/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages").isArray())
                .andExpect(jsonPath("$.messages.size()").value(3))
                .andExpect(jsonPath("$.messages", containsInAnyOrder(
                        "The id of the product is required.",
                        "The id of the user is required.",
                        "Quantity must be positive")));
    }

    @Test
    @DisplayName("Test for 401 response when adding a cart item by an unauthenticated user")
    void shouldGet401StatusCode_WhenAddCartItemByUnauthenticatedUser() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(post("/api/v1/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"productId\": 3," +
                                " \"userId\": 2," +
                                " \"quantity\": 5.5}"
                        ))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Sql(scripts = "classpath:db/populateDB.sql")
    @Sql(scripts = "classpath:db/dropDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails("userTest")
    @jakarta.transaction.Transactional
    @DisplayName("Test get cart item when update cart item success")
    void shouldReturnCartItem_WhenUpdateCartItem() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(put("/api/v1/cart/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"productId\": 2," +
                                " \"userId\": 2," +
                                " \"quantity\": 7.0}"
                        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("3"))
                .andExpect(jsonPath("$.product").exists())
                .andExpect(jsonPath("$.product.id").value("2"))
                .andExpect(jsonPath("$.product.name").value("Product 2"))
                .andExpect(jsonPath("$.product.description").value("Description 2"))
                .andExpect(jsonPath("$.product.price").value("5.0"))
                .andExpect(jsonPath("$.product.quantity").value("7.0"))
                .andExpect(jsonPath("$.userId").value("2"))
                .andExpect(jsonPath("$.quantity").value(7.0));
    }

    @Test
    @Sql(scripts = "classpath:db/populateDB.sql")
    @Sql(scripts = "classpath:db/dropDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails("userTest")
    @jakarta.transaction.Transactional
    @DisplayName("Test get 400 when update a cart item without required payload fields")
    void shouldThrowBadRequest_WhenUpdateCartItemWithoutPayload() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(put("/api/v1/cart/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages").isArray())
                .andExpect(jsonPath("$.messages.size()").value(3))
                .andExpect(jsonPath("$.messages", containsInAnyOrder(
                        "The id of the product is required.",
                        "The id of the user is required.",
                        "Quantity must be positive")));
    }

    @Test
    @Sql(scripts = "classpath:db/populateDB.sql")
    @Sql(scripts = "classpath:db/dropDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails("userTest")
    @jakarta.transaction.Transactional
    @DisplayName("Test get 400 when update a cart item with invalid combination of payload fields")
    void shouldThrowBadRequest_WhenUpdateCartItemWithInvalidPayload() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(put("/api/v1/cart/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"productId\": 1," +
                                " \"userId\": 2," +
                                " \"quantity\": 7.0}"
                        ))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cart item record with such combination of cartItemId, productId and userId does not exist"));
    }

    @Test
    @Sql(scripts = "classpath:db/populateDB.sql")
    @Sql(scripts = "classpath:db/dropDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails("userTest")
    @jakarta.transaction.Transactional
    @DisplayName("Test get 400 when update a cart item with non-existing product")
    void shouldThrowBadRequest_WhenUpdateCartItemWithInvalidProductId() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(put("/api/v1/cart/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"productId\": 7," +
                                " \"userId\": 2," +
                                " \"quantity\": 7.0}"
                        ))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Product with id: 7 does not exist"));
    }

    @Test
    @Sql(scripts = "classpath:db/populateDB.sql")
    @Sql(scripts = "classpath:db/dropDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails("userTest")
    @jakarta.transaction.Transactional
    @DisplayName("Test get 400 when update a cart item and product stock is insufficient")
    void shouldThrowBadRequest_WhenUpdateCartItemInsufficientStock() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(put("/api/v1/cart/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"productId\": 2," +
                                " \"userId\": 2," +
                                " \"quantity\": 100.0}"
                        ))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Product stock is not sufficient"));
    }

    @Test
    @Sql(scripts = "classpath:db/populateDB.sql")
    @Sql(scripts = "classpath:db/dropDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails("adminTest")
    @jakarta.transaction.Transactional
    @DisplayName("Test get 400 when updating a cart item for a user that does not match the logged-in user")
    void shouldThrowBadRequest_WhenUpdateCartItemWithInvalidUserId() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(put("/api/v1/cart/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"productId\": 2," +
                                " \"userId\": 2," +
                                " \"quantity\": 7.0}"
                        ))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User id 2 does not match logged in user id"));
    }

    @Test
    @DisplayName("Test for 401 response when updating a cart item by an unauthenticated user")
    void shouldGet401StatusCode_WhenUpdateCartItemByUnauthenticatedUser() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(put("/api/v1/cart/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"productId\": 3," +
                                " \"userId\": 2," +
                                " \"quantity\": 5.5}"
                        ))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Sql(scripts = "classpath:db/populateDB.sql")
    @Sql(scripts = "classpath:db/dropDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails("userTest")
    @jakarta.transaction.Transactional
    @DisplayName("Test get all cart items when get all user's cart items success")
    void shouldReturnListOfCartItems_WhenGetAllCartItems() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(get("/api/v1/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value("3"))
                .andExpect(jsonPath("$[0].product").exists())
                .andExpect(jsonPath("$[0].product.id").value("2"))
                .andExpect(jsonPath("$[0].product.name").value("Product 2"))
                .andExpect(jsonPath("$[0].product.description").value("Description 2"))
                .andExpect(jsonPath("$[0].product.price").value("5.0"))
                .andExpect(jsonPath("$[0].product.quantity").value("7.0"))
                .andExpect(jsonPath("$[0].userId").value("2"))
                .andExpect(jsonPath("$[0].quantity").value(7.0));
    }

    @Test
    @Sql(scripts = "classpath:db/populateDB.sql")
    @Sql(scripts = "classpath:db/dropDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails("userTestWithEmptyCart")
    @jakarta.transaction.Transactional
    @DisplayName("Test get empty array when get all cart items for empty cart")
    void shouldReturnEmptyList_WhenGetAllCartItemsEmptyCart() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(get("/api/v1/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    @DisplayName("Test for 401 response when getting all cart items by an unauthenticated user")
    void shouldGet401StatusCode_WhenGetAllCartItemsByUnauthenticatedUser() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(get("/api/v1/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Sql(scripts = "classpath:db/populateDB.sql")
    @Sql(scripts = "classpath:db/dropDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails("userTest")
    @jakarta.transaction.Transactional
    @DisplayName("Test get 200 status when delete cart item by authenticated user success")
    void shouldReturn200StatusCode_WhenDeleteCartItem() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(delete("/api/v1/cart/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{}"))
                .andExpect(status().isOk());
        Optional<CartItem> deletedCartItem = cartRepository.findById(3L);
        assertTrue(deletedCartItem.isEmpty(), "CartItem should be deleted");
    }

    @Test
    @DisplayName("Test for 401 response when deleting a cart item by an unauthenticated user")
    void shouldGet401StatusCode_WhenDeleteProductUnauthenticatedUser() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(delete("/api/v1/cart/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Sql(scripts = "classpath:db/populateDB.sql")
    @Sql(scripts = "classpath:db/dropDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails("userTest")
    @jakarta.transaction.Transactional
    @DisplayName("Test get 400 when delete cart item with non-existing cart item id")
    void shouldThrowBadRequest_WhenDeleteProductWithNonExistingId() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(delete("/api/v1/cart/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cart item with id: 100 does not exist"));
    }
}