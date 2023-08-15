package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Product;
import org.example.services.InventoryService;
import org.example.services.impl.Inventory;
import org.example.services.impl.Purchase;

import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

public class InventoryManager {

    static void inventoryOptions() {
        System.out.println("1. Add Product to inventory");
        System.out.println("2. Purchase Product from inventory");
        System.out.println("3. Generate Report");
        System.out.println("4. Exit");
        System.out.println(" --- > Enter your choice: ");
    }

    public static void main(String[] args) throws IOException {
        InventoryService inventoryService = new Inventory();
        inventoryService.loadFiles();
        ObjectMapper mapper = new ObjectMapper();
        Scanner scanner = new Scanner(System.in);
        System.out.println("====================================");
        System.out.println("   Inventory Management System   ");
        System.out.println("====================================");
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        while (true) {
            System.out.println();
            inventoryOptions();
            int choice = scanner.nextInt();
            scanner.nextLine();
            if (choice == 1) {
                String prettyJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(inventoryService.addProduct(scanner, new Product()));
                System.out.println(prettyJson);
            }
            else if(choice == 2) {
                try {
                    Vector<Product> productVector = inventoryService.getAllProducts();
                    System.out.println("Select an product for purchase");
                    for (int index = 1; index <= productVector.size(); index++) {
                        System.out.println(index + ". " + productVector.elementAt(index - 1));
                    }
                    System.out.println((productVector.size() + 1) + ". Exit");
                    while (true) {
                        int purchaseChoice = scanner.nextInt();
                        if (purchaseChoice > productVector.size()) {
                            break;
                        }
                        System.out.println("Enter the product quantity: ");
                        Integer quantity = scanner.nextInt();
                        executorService.execute(new Purchase(inventoryService, productVector.elementAt(purchaseChoice - 1).getProductId(), quantity));
                    }
                    executorService.shutdown();
                    while (!executorService.isTerminated()) {
                    }
                    inventoryService.saveToFile();
                }
                catch (RejectedExecutionException rejectedExecutionException) {
                    System.out.println(rejectedExecutionException.getLocalizedMessage());
                }
            }
            else if(choice == 3) {
                String prettyJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(inventoryService.generateReport());
                System.out.println(prettyJson);
            }
            else if(choice == 4) {
                break;
            }
            else {
                break;
            }
        }

    }
}