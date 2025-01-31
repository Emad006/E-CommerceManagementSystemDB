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
import interfaces.controllers.ICardManager;

public class CardManager implements ICardManager {
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
            if (c.getCustomerEmail().equals(customerEmail) && c.getCardNumber().equals(cardNumber)) {
                iterator.remove();
                dumpDataToFile();
                return;
            }
        }
    }

    public ArrayList<Card> getCardsByCustomerEmail(String email) {
        ArrayList<Card> customerCards = new ArrayList<Card>();
        for (Card c : cardsOnFile) {
            if (c.getCustomerEmail().equals(email)) {
                customerCards.add(c);
            }
        }
        return customerCards;
    }

    public boolean cardExistsOnUserAccount(String customerEmail, String cardNumber) {
        for (Card c : cardsOnFile) {
            if (c.getCustomerEmail().equals(customerEmail) && c.getCardNumber().equals(cardNumber)) {
                return true;
            }
        }
        return false;
    }

    public String[][] getDataForTable(String email) {
        ArrayList<Card> customerCards = getCardsByCustomerEmail(email);
        String[][] data = new String[customerCards.size()][6];
        for (int i = 0; i < customerCards.size(); i++) {
            Card c = customerCards.get(i);
            data[i][0] = c.getCardNumber();
            data[i][1] = c.getExpiryDate();
            data[i][2] = Integer.toString(c.getSecurityCode());
            data[i][3] = c.getNameOnCard();
            data[i][4] = c.getBillingAddress();
            data[i][5] = c.getCustomerEmail();
        }
        return data;
    }

    private void loadCardsToMemory() {
        // Try to create file if it doesn't exist
        File file = new File("../txtdb/cardData.txt");
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Read from file
        try {
            BufferedReader reader = new BufferedReader(new FileReader("../txtdb/cardData.txt"));
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
            BufferedWriter writer = new BufferedWriter(new FileWriter("../txtdb/cardData.txt"));
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
