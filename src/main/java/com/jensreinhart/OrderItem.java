package com.jensreinhart;

public class OrderItem {

    private int articleId;
    private String description;
    private double quantity;
    private double unitPrice;
    private double tax;

    public OrderItem(int articleId, String description, double quantity, double unitPrice, double tax) {
        this.articleId = articleId;
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.tax = tax;
    }

    public int getArticleId() {
        return articleId;
    }

    public String getDescription() {
        return description;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getTax() {
        return tax;
    }
}
