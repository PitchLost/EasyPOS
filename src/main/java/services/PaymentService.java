package services;

import models.Order;
import models.OrderItem;
import java.math.BigDecimal;
import java.util.ArrayList;

/** The service for payment. This will be removed in the future to just use HomeService*/
//TODO: Remove this and just use HomeService
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

