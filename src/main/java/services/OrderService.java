package services;

import models.Order;

import java.util.ArrayList;

public class OrderService {
    private ArrayList<Order> orders;

    public void addOrder(Order order) {
        orders.add(order);
    }
    public ArrayList<Order> getOrders() {
        return orders;
    }

    private void cacheOrders() {

    }

    private void retrieveOrders() {

    }
}
