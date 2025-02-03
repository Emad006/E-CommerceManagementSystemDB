package core.entities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import database.dao.ProductDAO;
import interfaces.entities.ICart;

public class Cart implements ICart {
    private HashMap<Product, Integer> userCart;
    private String customerEmail;

    public Cart(String customerEmail) {
        userCart = new HashMap<Product, Integer>();
        this.customerEmail = customerEmail;
        loadCartToMemory();
    }

    // TODO: Validate quantity in front-end
    public void addProductToCart(Product p, int q) {
        if (q > 0) {
            if (!userCart.containsKey(p)) {
                userCart.put(p, q);
            } else {
                // If user clicks on item multiple times, add the quantity.
                userCart.put(p, userCart.get(p) + q);
            }

            saveCartToFile();
        } else {
            throw new IllegalArgumentException("Invalid Quantity.");
        }
    }

    public void removeProductFromCart(Product p) {
        userCart.remove(p);
        saveCartToFile();
    }

    // TODO: Validate quantity in front-end
    public void updateQuantity(Product p, int q) {
        if (q > 0) {
            userCart.put(p, q);
        } else if (q == 0) {
            removeProductFromCart(p);
        } else {
            throw new IllegalArgumentException("Invalid Quantity.");
        }

        saveCartToFile();
    }

    // Use this method for validating if product quantity in cart is greater than
    // stock
    public int getProductQuantityInCart(Product p) {
        // In case the product is not in the cart, return 0
        return userCart.getOrDefault(p, 0);
    }

    public HashMap<Product, Integer> getCartHashMap() {
        return new HashMap<Product, Integer>(userCart);
    }

    public boolean isEmpty() {
        return userCart.isEmpty();
    }

    public double getTotal() {
        double total = 0;
        for (Product p : userCart.keySet()) {
            total += p.getPrice() * userCart.get(p);
        }
        return total;
    }

    public void clearCart() {
        userCart.clear();
        saveCartToFile();
    }

    // Load cart from database
    private void loadCartToMemory() {
        // Try to create file if it doesn't exist
        File file = new File("../txtdb/cartData.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Read from file
        try {
            BufferedReader reader = new BufferedReader(new FileReader("../txtdb/cartData.txt"));
            ProductDAO productDAO = new ProductDAO();
            String l;
            while ((l = reader.readLine()) != null) {
                String[] parts = l.split("\\^~\\^");

                // Only add product to cart if it belongs to the customer
                if (parts[0].equals(customerEmail)) {
                    Product p = productDAO.searchProduct(Integer.parseInt(parts[1]));
                    if (p.getStock() >= Integer.parseInt(parts[2])) {
                        addProductToCart(p, Integer.parseInt(parts[2]));
                    } else if (p.getStock() > 0) {
                        addProductToCart(p, p.getStock());
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Save cart to databse
    private void saveCartToFile() {
        File inputFile = new File("../txtdb/cartData.txt");
        File tempFile = new File("../txtdb/cartDataTemp.txt");

        // Create inputFile if it doesn't exist
        if(!inputFile.exists()) {
            try {
                inputFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Create tempFile if it doesn't exist
        if(!tempFile.exists()) {
            try {
                tempFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("\\^~\\^");

                    // Only write to tempFile if it doesn't belong to the customer
                    if (!parts[0].equals(customerEmail)) {
                        writer.write(line);
                        writer.newLine();
                    }
                }

                // Write the latest cart of the customer to the tempFile
                for (Product p : userCart.keySet()) {
                    writer.write(customerEmail + "^~^" + p.getID() + "^~^" + userCart.get(p));
                    writer.newLine();
                }

                reader.close();
                writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!inputFile.delete()) {
            System.out.println("Could not delete old 'cartData.txt' file");
            return;
        }

        if (!tempFile.renameTo(inputFile)) {
            System.out.println("Could not rename 'cartDataTemp.txt' to 'cartData.txt'");
        }
    }
}
