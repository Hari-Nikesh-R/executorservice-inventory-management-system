package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.exception.InputExceptionHandler;
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

/**
 * @author harinikeshr
 */
public class InventoryManager {

    static void inventoryOptions() {
        System.out.println("1. Add Product to inventory");
        System.out.println("2. Purchase Product from inventory");
        System.out.println("3. Generate Report");
        System.out.println("4. Exit");
        System.out.println(" --- > Enter your choice: ");
    }

    static void baseApplication(InventoryService inventoryService, ObjectMapper mapper, Scanner scanner) throws IOException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        while (true) {
            System.out.println();
            inventoryOptions();
            int choice = scanner.nextInt();
            scanner.nextLine();
            if (choice == 1) {
                addProductsToInventory(mapper, inventoryService, scanner);
            }
            else if(choice == 2) {
                purchaseProductFromInventory(inventoryService, executorService, scanner);
            }
            else if(choice == 3) {
               generateReport(inventoryService, mapper);
            }
            else if(choice == 4) {
                break;
            }
            else {
                break;
            }
        }
    }

    static void addProductsToInventory(ObjectMapper mapper, InventoryService inventoryService, Scanner scanner) throws IOException {
        String prettyJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(inventoryService.addProduct(scanner, new Product()));
        System.out.println(prettyJson);
    }

    static void loadAllProduct(Vector<Product> productVector, InventoryService inventoryService) {
        System.out.println("Select an product for purchase");
        for (int index = 1; index <= productVector.size(); index++) {
            System.out.println(index + ". " + productVector.elementAt(index - 1));
        }
        System.out.println((productVector.size() + 1) + ". Exit");
    }

    static void purchaseProductFromInventory(InventoryService inventoryService, ExecutorService executorService, Scanner scanner) {
        try {
            Vector<Product> productVector = inventoryService.getAllProducts();
            loadAllProduct(productVector, inventoryService);
            while (true) {
                int purchaseChoice = new InputExceptionHandler<Integer>(scanner).getUserInput("Invalid choice please try numbers, Please re-enter it", Integer.class, "");
                if (purchaseChoice > productVector.size()) {
                    break;
                }
                if (productVector.elementAt(purchaseChoice - 1).getProductQuantity()==0) {
                    System.out.println("Product is Out of Stock");
                    loadAllProduct(inventoryService.getAllProducts(), inventoryService);
                }
                else {
                    Integer quantity = new InputExceptionHandler<Integer>(scanner).getUserInput("Invalid quantity, Please re-enter it", Integer.class, "Enter the product quantity: ");
                    executorService.execute(new Purchase(inventoryService, productVector.elementAt(purchaseChoice - 1).getProductId(), quantity));
                }
            }
            executorService.shutdown();
            while (!executorService.isTerminated()){}
            inventoryService.saveToFile();
        }
        catch (RejectedExecutionException rejectedExecutionException) {
            System.out.println(rejectedExecutionException.getLocalizedMessage());
        }
    }

    static void generateReport(InventoryService inventoryService, ObjectMapper mapper) throws JsonProcessingException {
        String prettyJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(inventoryService.generateReport());
        System.out.println(prettyJson);
    }

    public static void main(String[] args) throws IOException {
        InventoryService inventoryService = new Inventory();
        inventoryService.loadFiles();
        ObjectMapper mapper = new ObjectMapper();
        Scanner scanner = new Scanner(System.in);
        System.out.println("====================================");
        System.out.println("   Inventory Management System   ");
        System.out.println("====================================");
        baseApplication(inventoryService, mapper, scanner);
    }
}