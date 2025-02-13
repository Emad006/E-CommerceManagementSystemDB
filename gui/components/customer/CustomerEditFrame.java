package gui.components.customer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import core.entities.Customer;
import database.dao.UserDAO;

public class CustomerEditFrame extends JFrame {
    private Customer customer;
    private UserDAO userDAO;

    public CustomerEditFrame(Customer customer) {
        this.customer = customer;
        userDAO = new UserDAO();

        // Set frame title
        setTitle("Edit Customer Account Information");
        setSize(450, 560);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame
        setLayout(new BorderLayout());

        // Create toggle button
        JButton toggleButton = createStyledButton("Edit Payment Info", new Color(80, 80, 80), Color.WHITE, new Font("Arial", Font.BOLD, 14), new Color(100, 100, 100));
        toggleButton.setPreferredSize(new Dimension(450, 40)); // Minimal height
        toggleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new CardEditFrame(customer);
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
        Color disabledFieldBgColor = new Color(80, 80, 80);

        // Create fields and labels
        JLabel nameLabel = createCenteredLabel("Name", labelFont, labelTextColor);
        JTextField nameField = createStyledTextField(20, fieldFont, fieldBgColor, fieldFgColor);
        nameField.setText(customer.getName());

        JLabel emailLabel = createCenteredLabel("Email", labelFont, labelTextColor);
        JTextField emailField = createStyledTextField(20, fieldFont, disabledFieldBgColor, fieldFgColor);
        emailField.setText(customer.getEmail());
        emailField.setEnabled(false); // Disable the email field

        JLabel passwordLabel = createCenteredLabel("Password", labelFont, labelTextColor);
        JPasswordField passwordField = createStyledPasswordField(20, fieldFont, fieldBgColor, fieldFgColor);
        passwordField.setText(customer.getPassword());

        JLabel repeatPasswordLabel = createCenteredLabel("Repeat Password", labelFont, labelTextColor);
        JPasswordField repeatPasswordField = createStyledPasswordField(20, fieldFont, fieldBgColor, fieldFgColor);
        repeatPasswordField.setText(customer.getPassword());

        JLabel genderLabel = createCenteredLabel("Gender", labelFont, labelTextColor);
        JComboBox<String> genderComboBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderComboBox.setFont(fieldFont);
        genderComboBox.setBackground(fieldBgColor);
        genderComboBox.setForeground(fieldFgColor);
        genderComboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        genderComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, genderComboBox.getPreferredSize().height));

        if (customer.getGender().equalsIgnoreCase("Male")) {
            genderComboBox.setSelectedIndex(0);
        } else if (customer.getGender().equalsIgnoreCase("Female")) {
            genderComboBox.setSelectedIndex(1);
        } else {
            genderComboBox.setSelectedIndex(2);
        }

        JLabel contactNoLabel = createCenteredLabel("Contact Number", labelFont, labelTextColor);
        JTextField contactNoField = createStyledTextField(20, fieldFont, fieldBgColor, fieldFgColor);
        contactNoField.setText(customer.getContactNo());

        JLabel addressLabel = createCenteredLabel("Address", labelFont, labelTextColor);
        JTextField addressField = createStyledTextField(20, fieldFont, fieldBgColor, fieldFgColor);
        addressField.setText(customer.getAddress());

        // Create buttons with color and add action listeners
        JButton saveButton = createStyledButton("Save", buttonBgColor, buttonTextColor, fieldFont, buttonHoverBgColor);
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String password = new String(passwordField.getPassword());
                String repeatPassword = new String(repeatPasswordField.getPassword());
                String gender = (String) genderComboBox.getSelectedItem();
                String contactNo = contactNoField.getText();
                String address = addressField.getText();
                editUser(name, password, repeatPassword, gender, contactNo, address);
            }
        });

        JButton cancelButton = createStyledButton("Cancel", buttonBgColor, buttonTextColor, fieldFont, buttonHoverBgColor);
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // Add components to panel
        panel.add(nameLabel);
        panel.add(nameField);

        panel.add(emailLabel);
        panel.add(emailField);

        panel.add(passwordLabel);
        panel.add(passwordField);

        panel.add(repeatPasswordLabel);
        panel.add(repeatPasswordField);

        panel.add(genderLabel);
        panel.add(genderComboBox);

        panel.add(contactNoLabel);
        panel.add(contactNoField);

        panel.add(addressLabel);
        panel.add(addressField);

        // Button panel with FlowLayout and spacing
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(new Color(40, 40, 40)); // Match the background color
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

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

    // Method to create a styled password field with dark mode colors
    private JPasswordField createStyledPasswordField(int columns, Font font, Color bgColor, Color fgColor) {
        JPasswordField passwordField = new JPasswordField(columns);
        passwordField.setFont(font);
        passwordField.setBackground(bgColor);
        passwordField.setForeground(fgColor);
        passwordField.setCaretColor(fgColor);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, passwordField.getPreferredSize().height));
        return passwordField;
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

    private void editUser(String name, String password, String repeatPassword, String gender, String contactNo,
                          String address) {
        // Empty field validation
        if (name.isEmpty() || password.isEmpty() || repeatPassword.isEmpty() || contactNo.isEmpty()
                || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all the fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Password match validation
        if (!password.equals(repeatPassword)) {
            System.out.println("Passwords do not match.");
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Password length validation
        if (password.length() < 8) {
            JOptionPane.showMessageDialog(this, "Password must be at least 8 characters long.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Contact number validation
        if (contactNo.length() >= 10 && contactNo.length() <= 20) {
            JOptionPane.showMessageDialog(this, "Invalid contact number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Proceed to update customer
        if (userDAO.updateUser(customer.getID(), name, customer.getEmail(), password, customer.getRole(), gender, contactNo,
                address)) {
            JOptionPane.showMessageDialog(this, "Details updated successfully.", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update details.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}