package gui.components.customer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import core.entities.Customer;
import core.entities.Card;
import database.dao.CardDAO;

public class PaymentFrame extends JFrame {
    private JTextField newAddressField;
    private JTextField cardNumberField;
    private JTextField expiryDateField;
    private JTextField securityCodeField;
    private JTextField nameOnCardField;
    private JComboBox<String> savedCardsComboBox;
    private JCheckBox sameAsDelivery;
    private CardDAO cardDAO;
    private Customer customer;

    public PaymentFrame(Customer customer, CartFrame cartFrame) {
        this.customer = customer;
        cardDAO = new CardDAO();

        // Set the frame properties
        setTitle("Payment");
        setSize(900, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null); // Use absolute layout (null layout)

        // Create a header
        JLabel header = new JLabel("Payment", JLabel.CENTER);
        header.setFont(new Font("Verdana", Font.BOLD, 25));
        header.setBounds(300, 20, 300, 30); // Manually set position and size

        // Payment Details label
        JLabel paymentDetailsLabel = new JLabel("Payment Details");
        paymentDetailsLabel.setFont(new Font("Verdana", Font.BOLD, 21));
        paymentDetailsLabel.setForeground(Color.decode("#ff6600"));
        paymentDetailsLabel.setBounds(50, 80, 200, 30);

        // Saved Cards ComboBox
        JLabel savedCardsLabel = new JLabel("Saved Cards:");
        savedCardsLabel.setFont(new Font("Verdana", Font.PLAIN, 18));
        savedCardsLabel.setBounds(50, 130, 200, 30);
        add(savedCardsLabel);

        savedCardsComboBox = new JComboBox<>();
        savedCardsComboBox.setFont(new Font("Verdana", Font.PLAIN, 17));
        savedCardsComboBox.setBounds(250, 130, 545, 30);
        add(savedCardsComboBox);

        // Load saved cards
        loadSavedCards();

        savedCardsComboBox.addActionListener(e -> {
            if (savedCardsComboBox.getSelectedItem().equals("Enter New Payment Method")) {
                enableCardFields(true);
                clearCardFields();
                sameAsDelivery.setEnabled(true);
                newAddressField.setEditable(true);
            } else {
                enableCardFields(false);
                populateCardFields(savedCardsComboBox.getSelectedItem().toString());
                sameAsDelivery.setEnabled(false);
                newAddressField.setEditable(false);
            }
        });

        // Card number input with placeholder and card icon
        JLabel cardNumberLabel = new JLabel("Card Number:");
        cardNumberLabel.setBounds(50, 180, 150, 30);
        cardNumberLabel.setFont(new Font("Verdana", Font.PLAIN, 18));

        cardNumberField = new JTextField(20);
        cardNumberField.setToolTipText("Enter your 16-digit card number without spaces or dashes");
        cardNumberField.setFont(new Font("Verdana", Font.PLAIN, 17));
        cardNumberField.setBounds(210, 180, 545, 30); // Adjusted width and height

        // Expiry date and security code side-by-side
        JLabel expiryDateLabel = new JLabel("Expiry Date:");
        expiryDateLabel.setBounds(50, 230, 130, 30);
        expiryDateLabel.setFont(new Font("Verdana", Font.PLAIN, 18));
        expiryDateField = new JTextField(5);
        expiryDateField.setToolTipText("Enter the expiry date in MM/YY format");
        expiryDateField.setFont(new Font("Verdana", Font.PLAIN, 17));
        expiryDateField.setBounds(210, 230, 160, 30); // Set position and size

        JLabel securityCodeLabel = new JLabel("Security Code:");
        securityCodeLabel.setBounds(440, 230, 140, 30);
        securityCodeLabel.setFont(new Font("Verdana", Font.PLAIN, 18));
        securityCodeField = new JTextField(3);
        securityCodeField.setToolTipText("3-digit code on the back of your card");
        securityCodeField.setFont(new Font("Verdana", Font.PLAIN, 17));
        securityCodeField.setBounds(600, 230, 155, 30); // Set position and size

        // Name on card - Adjust size
        JLabel nameOnCardLabel = new JLabel("Name on Card:");
        nameOnCardLabel.setBounds(50, 280, 140, 30);
        nameOnCardLabel.setFont(new Font("Verdana", Font.PLAIN, 18));
        nameOnCardField = new JTextField();
        nameOnCardField.setToolTipText("Enter the exact name appears on card");
        nameOnCardField.setFont(new Font("Verdana", Font.PLAIN, 17));
        nameOnCardField.setBounds(210, 280, 545, 30); // Set position and size

        // Billing address and checkbox
        JLabel billingAddressLabel = new JLabel("Billing Address:");
        billingAddressLabel.setBounds(50, 330, 200, 30);
        billingAddressLabel.setFont(new Font("Verdana", Font.BOLD, 20));
        sameAsDelivery = new JCheckBox("Same as my delivery address");
        sameAsDelivery.setBounds(50, 370, 350, 30);
        sameAsDelivery.setFont(new Font("Verdana", Font.PLAIN, 18));
        sameAsDelivery.setFocusPainted(false);
        sameAsDelivery.addActionListener(e -> {
            if (sameAsDelivery.isSelected()) {
                newAddressField.setText(customer.getAddress());
                newAddressField.setEditable(false);
            } else if (!sameAsDelivery.isSelected()) {
                newAddressField.setText("");
                newAddressField.setEditable(true);
            }
        });

        // Billing Address Information
        JLabel billingInfoLabel = new JLabel();
        billingInfoLabel.setBounds(50, 430, 350, 30);
        billingInfoLabel.setForeground(Color.decode("#ff6600"));
        billingInfoLabel.setFont(new Font("Verdana", Font.BOLD, 21));
        newAddressField = new JTextField("");
        newAddressField.setFont(new Font("Verdana", Font.PLAIN, 17));
        newAddressField.setBounds(50, 490, 702, 30); // Set position and size
        newAddressField.setBorder(BorderFactory.createCompoundBorder(
                newAddressField.getBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5))); // Adjust padding

        JLabel totalLabel = new JLabel("Total Cost:");
        totalLabel.setFont(new Font("Verdana", Font.BOLD, 21));
        totalLabel.setForeground(Color.decode("#000000"));
        totalLabel.setBounds(50, 550, 400, 30);

        JLabel amountLabel = new JLabel("$" + String.format("%.2f", customer.getCart().getTotal() * 1.13));
        amountLabel.setFont(new Font("Verdana", Font.BOLD, 21));
        amountLabel.setForeground(Color.decode("#ff221e"));
        amountLabel.setBounds(600, 550, 200, 30);

        // Two buttons: Pay Now and Clear
        JButton payNowButton = new JButton("Pay Now");
        payNowButton.setBounds(250, 640, 445, 40); // Set position and size
        payNowButton.setBackground(Color.decode("#000000"));
        payNowButton.setForeground(Color.WHITE);
        payNowButton.setFont(new Font("Verdana", Font.BOLD, 17));
        payNowButton.addActionListener(e -> {
            // Validate the input fields
            if (cardNumberField.getText().length() != 16) {
                JOptionPane.showMessageDialog(null, "Please enter a valid 16-digit card number", "Invalid Card Number",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } else if (expiryDateField.getText().length() != 5) {
                JOptionPane.showMessageDialog(null, "Please enter a valid expiry date in MM/YY format",
                        "Invalid Expiry Date", JOptionPane.ERROR_MESSAGE);
                return;
            } else if (securityCodeField.getText().length() != 3) {
                JOptionPane.showMessageDialog(null, "Please enter a valid 3-digit security code",
                        "Invalid Security Code", JOptionPane.ERROR_MESSAGE);
                return;
            } else if (nameOnCardField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter the name on the card", "Invalid Name",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } else if (newAddressField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter the billing address", "Invalid Address",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } else {
                JOptionPane.showMessageDialog(null, "Payment successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                cartFrame.paymentSuccessful();
                cartFrame.dispose();
            }
        });

        JButton clearButton = new JButton("Clear");
        clearButton.setBounds(250, 690, 445, 40); // Set position and size
        clearButton.setBackground(Color.decode("#e32f16"));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFont(new Font("Verdana", Font.BOLD, 17));
        clearButton.addActionListener(e -> {
            cardNumberField.setText("");
            expiryDateField.setText("");
            securityCodeField.setText("");
            nameOnCardField.setText("");
            if (sameAsDelivery.isSelected()) {
                newAddressField.setText(customer.getAddress());
            } else {
                newAddressField.setText("");
            }
        });

        // Add components to the frame
        add(header);
        add(paymentDetailsLabel);
        add(savedCardsLabel);
        add(savedCardsComboBox);
        add(cardNumberLabel);
        add(cardNumberField);
        add(expiryDateLabel);
        add(expiryDateField);
        add(securityCodeLabel);
        add(securityCodeField);
        add(nameOnCardLabel);
        add(nameOnCardField);
        add(billingAddressLabel);
        add(sameAsDelivery);
        add(billingInfoLabel);
        add(newAddressField);
        add(totalLabel);
        add(amountLabel);
        add(payNowButton);
        add(clearButton);

        // Make frame visible
        setVisible(true);
    }

    private void loadSavedCards() {
        ArrayList<Card> savedCards = cardDAO.getCardsByCustomerEmail(customer.getEmail());
        savedCardsComboBox.addItem("Enter New Payment Method");
        for (Card card : savedCards) {
            savedCardsComboBox.addItem(card.getCardNumber() + " - " + card.getExpiryDate());
        }
    }

    private void enableCardFields(boolean enable) {
        cardNumberField.setEnabled(enable);
        expiryDateField.setEnabled(enable);
        securityCodeField.setEnabled(enable);
        nameOnCardField.setEnabled(enable);
    }

    private void clearCardFields() {
        cardNumberField.setText("");
        expiryDateField.setText("");
        securityCodeField.setText("");
        nameOnCardField.setText("");
        newAddressField.setText("");
    }

    private void populateCardFields(String cardDetails) {
        ArrayList<Card> savedCards = cardDAO.getCardsByCustomerEmail(customer.getEmail());
        for (Card card : savedCards) {
            if ((card.getCardNumber() + " - " + card.getExpiryDate()).equals(cardDetails)) {
                cardNumberField.setText(card.getCardNumber());
                expiryDateField.setText(card.getExpiryDate());
                securityCodeField.setText(String.valueOf(card.getSecurityCode()));
                nameOnCardField.setText(card.getNameOnCard());
                newAddressField.setText(card.getBillingAddress());
                break;
            }
        }
    }
}