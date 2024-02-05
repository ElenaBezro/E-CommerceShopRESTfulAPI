package com.bezro.shopRESTfulAPI.constants;

public class ResponseMessages {
    public static final String ADD_PRODUCT_BAD_REQUEST_MESSAGE = "[\n" +
            "        \"Price must be greater than zero\",\n" +
            "        \"Stock quantity must be greater than zero\",\n" +
            "        \"The name is required.\"\n" +
            "    ]";

    public static final String UPDATE_PRODUCT_BAD_REQUEST_MESSAGE = "[\n" +
            "        \"Price must be greater than zero\",\n" +
            "        \"Stock quantity must be greater than zero\",\n" +
            "        \"The name is required.\"\n" +
            "    ]";
    public static final String UPDATE_ORDER_BAD_REQUEST_MESSAGE = "[\n" +
            "        \"Invalid order status.\"\n" +
            "    ]";
    public static final String REGISTER_BAD_REQUEST_MESSAGE = "[\n" +
            "        \"Password must be 8 characters long and combination of uppercase letters, lowercase letters, numbers, special characters.\",\n" +
            "        \"Password and Confirm Password must be matched!\",\n" +
            "        \"The email is not a valid email.\",\n" +
            "        \"The username must be from 3 to 20 characters.\"\n" +
            "    ]";

    public static final String ADD_CART_ITEM_BAD_REQUEST_MESSAGE = "[\n" +
            "        \"The id of the product is required\",\n" +
            "        \"The id of the user is required\",\n" +
            "        \"The quantity is required. Quantity must be positive\"\n" +
            "    ]";
}
