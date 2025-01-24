package interfaces.controllers;

import java.util.ArrayList;
import core.entities.Card;

public interface ICardManager {
    public void addCard(String customerEmail, String cardNumber, String expiryDate, int securityCode, String nameOnCard, String billingAddress);
    public void deleteCard(String customerEmail, String cardNumber);
    public ArrayList<Card> getCardsByCustomerEmail(String email);
}
