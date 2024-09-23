package core.entities;

import java.util.HashMap;

import interfaces.controllers.ICart;

public class Cart implements ICart {
    private HashMap<Product, Integer> userCart;

    public Cart() {
        userCart = new HashMap<Product, Integer>();
    }

    // TODO: Validate quantity in front-end
    public void addProductToCart(Product p, int q) {
        if (q > 0) {
            if (!userCart.containsKey(p)) {
                userCart.put(p, q);
                // System.out.println("Product added to cart: " + p.getName());
                System.out.println(userCart);
            } else {
                // If user clicks on item multiple times, add the quantity.
                userCart.put(p, userCart.get(p) + q);
                // System.out.println("Product added to cart: " + p.getName());
                System.out.println(userCart);
            }
        } else {
            throw new IllegalArgumentException("Invalid Quantity.");
        }
    }

    public void removeProductFromCart(Product p) {
        userCart.remove(p);
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
    }

    // Use this method for validating if product quantity in cart is greater than stock
    // TODO: Handle null returns
    public int getProductQuantityInCart(Product p) {
        // In case the product is not in the cart, return 0
        return userCart.getOrDefault(p, 0);
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
    }
}