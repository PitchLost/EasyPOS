package services;

import models.OrderItem;
import java.math.BigDecimal;
import java.util.ArrayList;

public class PaymentService {
    BigDecimal totalDue;
    ArrayList<OrderItem> orderItems;
    public PaymentService(BigDecimal due, ArrayList<OrderItem> items) {
        System.out.println("Total has been set: " + due);
        totalDue = due;
        orderItems = items;
    }
    public BigDecimal getTotalDue() {
        System.out.println("Total has been requested from payment service: " + this.totalDue);
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

