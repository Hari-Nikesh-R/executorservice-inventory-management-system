package org.example.services.impl;

import org.example.model.Product;
import org.example.services.InventoryService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.Vector;

public class Purchase implements Runnable {
    private final InventoryService inventoryService;
    private final String productId;
    private final Integer quantity;

    public Purchase(InventoryService inventoryService, String productId, Integer quantity) {
        this.inventoryService = inventoryService;
        this.productId = productId;
        this.quantity = quantity;
    }

    @Override
    public void run() {
        Product product = inventoryService.getProduct(productId).getData();
        if (Objects.nonNull(product) && product.getProductQuantity() >= quantity) {
            System.out.println(inventoryService.updateQuantity(productId, quantity));
            try {
                System.out.println("Purchase Successful");
                logPurchase(product, quantity);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Insufficient stock unable to purchase");
        }
        Vector<Product> productVector = inventoryService.getAllProducts();
        if (productVector.size() > 0) {
            System.out.println("Select an product for purchase");
            for (int index = 1; index <= productVector.size(); index++) {
                System.out.println(index + ". " + productVector.elementAt(index - 1));
            }
        }
        else {
            System.out.println("No Product Found to Purchase -  Quitting...");
        }
        System.out.println((productVector.size() + 1) + ". Exit");
    }

    private synchronized void logPurchase(Product product, Integer quantity) throws IOException {
        try {
            File file = new File("purchases.log");
            if(file.exists()) {
                writePurchasesLog(product, quantity);
            }
            else {
               if(file.createNewFile()) {
                    writePurchasesLog(product, quantity);
                }
               else {
                   System.out.println("Unable to write Logs");
               }
            }

        } catch (Exception exception) {
            System.out.println("Unable to write Logs");
            exception.printStackTrace();
        }
    }

    private void writePurchasesLog(Product product, Integer quantity) throws IOException {
        FileWriter fileWriter = new FileWriter("purchases.log", true);
        fileWriter.append("ID: ").append(product.getProductId()).append(" - Purchased: ").append(product.getProductName()).append(" of quantity ").append(String.valueOf(quantity));
        fileWriter.append("\n");
        fileWriter.close();
    }

}
