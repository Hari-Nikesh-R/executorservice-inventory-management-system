package org.example.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.model.Product;

@Data
@AllArgsConstructor
public class Report {
    private String productName;
    private Double totalPrice;
    private Integer quantity;
}
