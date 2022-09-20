package com.jensreinhart;

import java.util.ArrayList;
import java.util.List;

public class Order {

    private String date;
    private boolean isGerman;
    private List<OrderItem> orderItemList;

    public Order(String date, boolean isGerman) {
        this.date = date;
        this.isGerman = isGerman;
        this.orderItemList = new ArrayList<>();
    }

    public String getDate() {
        return date;
    }

    public List<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    public boolean getIsGerman(){
        return isGerman;
    }
}
