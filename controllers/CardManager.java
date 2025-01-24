package controllers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import core.entities.Card;

public class CardManager {
    private ArrayList <Card> cardsOnFile;

    public CardManager() {
        cardsOnFile = new ArrayList<Card>();
        loadCardsToMemory();
    }

    public void addCard(String customerEmail, String cardNumber, String expiryDate, int securityCode, String nameOnCard, String billingAddress) {
        Card c = new Card(customerEmail, cardNumber, expiryDate, securityCode, nameOnCard, billingAddress);
        cardsOnFile.add(c);
        dumpDataToFile();
    }

    public void deleteCard(String customerEmail, String cardNumber) {
        Iterator<Card> iterator = cardsOnFile.iterator();
        while (iterator.hasNext()) {
            Card c = iterator.next();
            if (c.getCustomerEmail() == customerEmail && c.getCardNumber() == cardNumber) {
                iterator.remove();
                dumpDataToFile();
                return;
            }
        }
    }

    public ArrayList<Card> getCardsByCustomerEmail(String email) {
        ArrayList<Card> customerCards = new ArrayList<Card>();
        for (Card c : cardsOnFile) {
            if (c.getCustomerEmail() == email) {
                customerCards.add(c);
            }
        }
        return customerCards;
    }

    private void loadCardsToMemory() {
        // Try to create file if it doesn't exist
        File file = new File("../database/cardData.txt");
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Read from file
        try {
            BufferedReader reader = new BufferedReader(new FileReader("../database/cardData.txt"));
            String l;
            while ((l = reader.readLine()) != null) {
                String[] parts = l.split("\\^~\\^");
                Card c = new Card(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]), parts[4], parts[5]);
                cardsOnFile.add(c);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Copying entire array list to text file
    private void dumpDataToFile() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("../database/cardData.txt"));
            for (Card c : cardsOnFile) {
                String t = c.getCustomerEmail() + "^~^" + c.getCardNumber() + "^~^" + c.getExpiryDate() + "^~^" + c.getSecurityCode() + "^~^" + c.getNameOnCard() + "^~^" + c.getBillingAddress();
                writer.write(t);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
