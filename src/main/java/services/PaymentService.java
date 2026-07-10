package services;

import models.Order;
import models.OrderItem;
import java.math.BigDecimal;
import java.util.ArrayList;

/** The service for payment. Used as a clean middleman between {@link HomeService} and controllers that need payment related fields*/
public class PaymentService {
    Order activeOrder;
    BigDecimal totalDue;
    public PaymentService(BigDecimal due, Order order) {
        totalDue = due;
        activeOrder = order;
    }
    public BigDecimal getTotalDue() {
        return totalDue;
    }
    public Order getActiveOrder() {
        return activeOrder;
    }
}

