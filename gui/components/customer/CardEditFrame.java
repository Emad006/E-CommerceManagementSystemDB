package gui.components.customer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.YearMonth;
import java.util.regex.Pattern;

import core.entities.Customer;
import controllers.CardManager;

public class CardEditFrame extends JFrame {
    private Customer customer;
    private CardManager cardManager;


    public CardEditFrame(Customer customer) {
        this.customer = customer;
        cardManager = new CardManager();

        // Set frame title
        setTitle("Edit Payment Information");
        setSize(450, 440);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame
        setLayout(new BorderLayout());

        // Create toggle button
        JButton toggleButton = createStyledButton("Edit Account Info", new Color(80, 80, 80), Color.WHITE, new Font("Arial", Font.BOLD, 14), new Color(100, 100, 100));
        toggleButton.setPreferredSize(new Dimension(450, 40)); // Minimal height
        toggleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new CustomerEditFrame(customer);
                dispose();
            }
        });

        // Create main panel for the form
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(40, 40, 40)); // Dark background
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding

        // Set font and color styling
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);
        Color fieldBgColor = new Color(60, 60, 60);
        Color fieldFgColor = Color.WHITE;
        Color buttonBgColor = new Color(80, 80, 80);
        Color buttonHoverBgColor = new Color(100, 100, 100);
        Color buttonTextColor = Color.WHITE;
        Color labelTextColor = new Color(200, 200, 200);

        // Create fields and labels
        JLabel cardNumberLabel = createCenteredLabel("Card Number", labelFont, labelTextColor);
        JTextField cardNumberField = createStyledTextField(20, fieldFont, fieldBgColor, fieldFgColor);

        JLabel expiryDateLabel = createCenteredLabel("Expiry Date (MM/YY)", labelFont, labelTextColor);
        JTextField expiryDateField = createStyledTextField(5, fieldFont, fieldBgColor, fieldFgColor);

        JLabel securityCodeLabel = createCenteredLabel("Security Code", labelFont, labelTextColor);
        JTextField securityCodeField = createStyledTextField(3, fieldFont, fieldBgColor, fieldFgColor);

        JLabel nameOnCardLabel = createCenteredLabel("Name on Card", labelFont, labelTextColor);
        JTextField nameOnCardField = createStyledTextField(20, fieldFont, fieldBgColor, fieldFgColor);

        JLabel billingAddressLabel = createCenteredLabel("Billing Address", labelFont, labelTextColor);
        JTextField billingAddressField = createStyledTextField(20, fieldFont, fieldBgColor, fieldFgColor);

        // Create buttons with color and add action listeners
        JButton addButton = createStyledButton("Add", buttonBgColor, buttonTextColor, fieldFont, buttonHoverBgColor);
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Logic to add card

                // Check if any fields are empty
                if (cardNumberField.getText().isEmpty() || expiryDateField.getText().isEmpty() || securityCodeField.getText().isEmpty() || nameOnCardField.getText().isEmpty() || billingAddressField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill out all fields", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Check if card number is valid
                if (cardNumberField.getText().length() != 16) {
                    JOptionPane.showMessageDialog(null, "Card number must be 16 digits", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Check if expiry date is valid
                if (!isValidExpiryDate(expiryDateField.getText())) {
                    JOptionPane.showMessageDialog(null, "Expiry date must be in MM/YY format", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Check if security code is valid
                if (Integer.parseInt(securityCodeField.getText()) < 100 && Integer.parseInt(securityCodeField.getText()) > 999) {
                    JOptionPane.showMessageDialog(null, "Security code must be between 100 and 999", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Check if card already added
                if (cardManager.cardExistsOnUserAccount(customer.getEmail(), cardNumberField.getText())) {
                    JOptionPane.showMessageDialog(null, "Card already exists", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Add card
                cardManager.addCard(customer.getEmail(), cardNumberField.getText(), expiryDateField.getText(), Integer.parseInt(securityCodeField.getText()), nameOnCardField.getText(), billingAddressField.getText());


                // Show success message
                JOptionPane.showMessageDialog(null, "Card added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);

                // Close the frame
                dispose();
            }
        });

        JButton deleteButton = createStyledButton("Delete", buttonBgColor, buttonTextColor, fieldFont, buttonHoverBgColor);
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Logic to delete card

                // Check if card number is empty
                if (cardNumberField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter a card number", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Check if card isn't added
                if (!cardManager.cardExistsOnUserAccount(customer.getEmail(), cardNumberField.getText())) {
                    JOptionPane.showMessageDialog(null, "Card doesn't exist", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Delete card
                cardManager.deleteCard(customer.getEmail(), cardNumberField.getText());

                // Show success message
                JOptionPane.showMessageDialog(null, "Card deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);

                // Close the frame
                dispose();
            }
        });

        JButton viewCardsButton = createStyledButton("View Cards", buttonBgColor, buttonTextColor, fieldFont, buttonHoverBgColor);
        viewCardsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showCardDetails();
            }
        });

        // Add components to panel
        panel.add(cardNumberLabel);
        panel.add(cardNumberField);

        panel.add(expiryDateLabel);
        panel.add(expiryDateField);

        panel.add(securityCodeLabel);
        panel.add(securityCodeField);

        panel.add(nameOnCardLabel);
        panel.add(nameOnCardField);

        panel.add(billingAddressLabel);
        panel.add(billingAddressField);

        // Button panel with FlowLayout and spacing
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(new Color(40, 40, 40)); // Match the background color
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewCardsButton);

        // Add button panel to main panel
        panel.add(buttonPanel);

        // Add components to frame
        add(toggleButton, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);

        // Make frame visible
        setVisible(true);
    }

    // Method to create a centered label with dark mode styling
    private JLabel createCenteredLabel(String text, Font font, Color textColor) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(font);
        label.setForeground(textColor);
        label.setAlignmentX(Component.CENTER_ALIGNMENT); // Center horizontally
        label.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Padding around label
        return label;
    }

    // Method to create a styled text field with dark mode colors
    private JTextField createStyledTextField(int columns, Font font, Color bgColor, Color fgColor) {
        JTextField textField = new JTextField(columns);
        textField.setFont(font);
        textField.setBackground(bgColor);
        textField.setForeground(fgColor);
        textField.setCaretColor(fgColor);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, textField.getPreferredSize().height));
        return textField;
    }

    // Method to create a styled button with dark mode colors and hover effect
    private JButton createStyledButton(String text, Color bgColor, Color textColor, Font font, Color hoverColor) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Change background on hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void showCardDetails() {
        // Create a new JFrame to display the card details
        JFrame cardDetailsFrame = new JFrame("Card Details");
        cardDetailsFrame.setSize(680, 440);
        cardDetailsFrame.setLocationRelativeTo(null); // Center the frame
        cardDetailsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Create column names
        String[] columnNames = {"Card Number", "Expiry Date", "Security Code", "Name on Card", "Billing Address"};

        // Create data array
        String[][] data = cardManager.getDataForTable(customer.getEmail());

        // Create table with data
        JTable table = new JTable(data, columnNames);
        table.setFillsViewportHeight(true);
        table.setForeground(Color.WHITE); // Set text color for the table
        table.setBackground(new Color(40, 40, 40)); // Set background color for the table

        // Make table scrollable
        JScrollPane scrollPane = new JScrollPane(table);
        cardDetailsFrame.add(scrollPane);

        // Make frame visible
        cardDetailsFrame.setVisible(true);
    }

    private boolean isValidExpiryDate(String dateStr) {

        // Validate the expiry date format
        Pattern pattern = Pattern.compile("^(0[1-9]|1[0-2])/[0-9]{2}$");
        if (!pattern.matcher(dateStr).matches()) {
            return false;
        }

        // Validate the expiry date (not expired)
        String[] parts = dateStr.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt(parts[1]);

        // Converting the two-digit year to a four-digit year
        int currentYear = YearMonth.now().getYear() % 100; // Get the last two digits of the year
        int currentMonth = YearMonth.now().getMonthValue();

        if (year > currentYear) {
            return true;
        } else if (year == currentYear && month > currentMonth) {
            return true;
        }

        return false;
    }
}