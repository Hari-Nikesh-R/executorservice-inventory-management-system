package org.example.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.dtos.response.Report;
import org.example.dtos.response.Response;
import org.example.model.Product;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

public interface InventoryService {
    Response<String> addProduct(Scanner scanner, Product product) ;
    Response<String> updateQuantity(String productId, Integer quantity);
    Response<Product> getProduct(String productId);
    Response<Map<String, List<Report>>> generateReport();
    Vector<Product> getAllProducts();
    void saveToFile();
    void loadFiles() throws IOException;
}
