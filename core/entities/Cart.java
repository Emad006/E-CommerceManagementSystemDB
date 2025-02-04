package core.entities;

import java.util.HashMap;

import database.dao.CartDAO;
import interfaces.entities.ICart;

public class Cart implements ICart {
    private CartDAO cartDAO;
    private int cartID;

    public Cart(String customerEmail) {
        cartDAO = new CartDAO();
        cartID = cartDAO.getCartID(customerEmail);
    }

    // TODO: Validate quantity in front-end
    public void addProductToCart(Product p, int q) {
        if (q > 0) {
            cartDAO.addProductToCart(cartID, p.getID(), q);
        } else {
            throw new IllegalArgumentException("Invalid Quantity.");
        }
    }

    public void removeProductFromCart(Product p) {
        cartDAO.removeProductFromCart(cartID, p.getID());
    }

    // TODO: Validate quantity in front-end
    public void updateQuantity(Product p, int q) {
        if (q > 0) {
            cartDAO.updateQuantity(cartID, p.getID(), q);
        } else if (q == 0) {
            removeProductFromCart(p);
        } else {
            throw new IllegalArgumentException("Invalid Quantity.");
        }
    }

    // Use this method for validating if product quantity in cart is greater than
    // stock
    public int getProductQuantityInCart(Product p) {
        // In case the product is not in the cart, return 0
        return cartDAO.getProductQuantityInCart(cartID, p.getID());
    }

    public HashMap<Product, Integer> getCartHashMap() {
        return cartDAO.getCartHashMap(cartID);
    }

    public boolean isEmpty() {
        return cartDAO.isEmpty(cartID);
    }

    public double getTotal() {
        return cartDAO.getTotal(cartID);
    }

    public void clearCart() {
        cartDAO.clearCart(cartID);
    }
}
