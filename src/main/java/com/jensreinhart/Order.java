package com.jensreinhart;

import java.util.ArrayList;
import java.util.List;

public class Order {

    private String date;
    private List<OrderItem> orderItemList;

    public Order(String date) {
        this.date = date;
        this.orderItemList = new ArrayList<>();
    }

    public String getDate() {
        return date;
    }

    public List<OrderItem> getOrderItemList() {
        return orderItemList;
    }
}
