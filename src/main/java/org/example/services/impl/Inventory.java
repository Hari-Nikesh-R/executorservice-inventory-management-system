package org.example.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dtos.response.Report;
import org.example.dtos.response.Response;
import org.example.exception.InputExceptionHandler;
import org.example.model.Product;
import org.example.services.InventoryService;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class Inventory implements InventoryService {

    private final Map<String, Product> products = new ConcurrentHashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Response<String> addProduct(Scanner scanner, Product product) throws IOException {
        product.setProductId(generateProductId());
        product.setProductName(new InputExceptionHandler<String>(scanner).getUserInput("Invalid Name, Please re-enter it", String.class, "Enter product name: "));
        product.setProductPrice(new InputExceptionHandler<Double>(scanner).getUserInput("Invalid Price, Please re-enter it", Double.class, "Enter product Price: "));
        product.setProductQuantity(new InputExceptionHandler<Integer>(scanner).getUserInput("Invalid quantity, Please re-enter it", Integer.class, "Enter product available quantity: "));
        products.put(product.getProductId(), product);
        saveToFile();
        return new Response<>(null, "Product added", true, null);
    }


    @Override
    public synchronized Response<String> updateQuantity(String productId, Integer quantity) {
        try {
            Product product = products.get(productId);
            if (Objects.nonNull(product)) {
                product.setProductQuantity(product.getProductQuantity() - quantity);
                saveToFile();
            }
            return new Response<>(null, "Quantity updated", true, null);
        } catch (Exception exception) {
            return new Response<>(null, "Quantity not updated", false, exception.getLocalizedMessage());
        }
    }

    @Override
    public Response<Product> getProduct(String productId) {
        return new Response<>(products.get(productId), "Product fetched", true, null);
    }

    @Override
    public Response<Map<String, List<Report>>> generateReport() {
        try {
            double totalValue = 0;
            Map<String, List<Report>> doubleListMap = new HashMap<>();
            List<Report> reports = new Vector<>();
            System.out.println("Id\t\t\tProduct Name\tPrice\t\tQuantity");
            for (Product product : products.values()) {
                totalValue += product.getProductPrice() * product.getProductQuantity();
                System.out.printf(
                        "%s\t\t%s\t\t%.4f\t\t%d%n",
                        product.getProductId(), product.getProductName(),
                        product.getProductPrice(), product.getProductQuantity());
                reports.add(new Report(product.getProductName(), product.getProductPrice(), product.getProductQuantity()));
            }
            doubleListMap.put("Total value of inventory: " + totalValue, reports);
            return new Response<>(doubleListMap, "Report generated", true, null);
        } catch (Exception exception) {
            return new Response<>(null, "Quantity not updated", false, exception.getLocalizedMessage());
        }
    }

    @Override
    public synchronized Vector<Product> getAllProducts() {
        return new Vector<>(products.values());
    }

    @Override
    public synchronized void saveToFile() {
        try (FileWriter fileWriter = new FileWriter("products.txt", true)) {

            clearFile();
            for (Product product : products.values()) {
                fileWriter.append(mapper.writeValueAsString(product));
                fileWriter.append("\n");
            }

        } catch (Exception ignore) {
        }
    }

    @Override
    public void loadFiles() throws IOException {
        try {
            File file = new File("products.txt");
            if (file.exists()) {
                readProductFile();
            } else {
                if (file.createNewFile()) {
                    readProductFile();
                } else {
                    System.out.println("Unable to read Logs");
                }
            }

        } catch (Exception exception) {
            boolean file = new File("products.txt").createNewFile();
            if (file) {
                readProductFile();
            }
        }
    }

    private void readProductFile() throws FileNotFoundException, JsonProcessingException {
        File file = new File("products.txt");
        try (FileReader fr = new FileReader(file)) {
            int content;
            StringBuilder text = new StringBuilder();
            while ((content = fr.read()) != -1) {
                if ((char) content == '\n') {
                    Product product = mapper.readValue(text.toString(), Product.class);
                    Product.idCounter++;
                    products.put(product.getProductId(), product);
                    System.out.println("File loaded: " + product);
                    text.setLength(0);
                } else {
                    text.append((char) content);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearFile() throws IOException {
        FileWriter fileWriter = new FileWriter("products.txt");
        fileWriter.write("");
        fileWriter.close();
    }

    synchronized String generateProductId() {
        String id = "P-ID-" + String.format("%02d", Product.idCounter);
        Product.idCounter++;
        return id;
    }
}
