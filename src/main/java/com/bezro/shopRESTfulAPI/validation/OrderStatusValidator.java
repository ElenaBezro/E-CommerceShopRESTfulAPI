package com.bezro.shopRESTfulAPI.validation;

import com.bezro.shopRESTfulAPI.entities.OrderStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class OrderStatusValidator implements ConstraintValidator<OrderStatusConstraint, String> {

    @Override
    public boolean isValid(String orderStatus, ConstraintValidatorContext context) {
        if (orderStatus == null) {
            return false;
        }

        try {
            OrderStatus.valueOf(orderStatus);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

}
