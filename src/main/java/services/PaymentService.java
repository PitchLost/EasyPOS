package services;

import models.Order;
import models.OrderItem;
import java.math.BigDecimal;
import java.util.ArrayList;

public class PaymentService {
    Order activeOrder;
    BigDecimal totalDue;
    ArrayList<OrderItem> orderItems;
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

