package services;

import models.OrderItem;
import java.math.BigDecimal;
import java.util.ArrayList;

public class PaymentService {
    BigDecimal totalDue;
    ArrayList<OrderItem> orderItems;
    public PaymentService(BigDecimal due, ArrayList<OrderItem> items) {
        totalDue = due;
        orderItems = items;
    }
    public BigDecimal getTotalDue() {
        return totalDue;
    }

    public ArrayList<OrderItem> getOrderItems() {
        return orderItems;
    }


    public String processPayment(BigDecimal totalDue, BigDecimal paymentAmount) {
        if (totalDue.compareTo(paymentAmount) > 0) {
            return "Payment failed. Insufficient funds.";
        }
         else {
            return "Payment successful.";
        }
    }
}

