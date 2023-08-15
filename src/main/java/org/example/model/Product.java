package org.example.model;

import lombok.Data;

@Data
public class Product {
    private String productId;
    private String productName;
    private Double productPrice;
    private Integer productQuantity;
    public static int idCounter = 1;
}
