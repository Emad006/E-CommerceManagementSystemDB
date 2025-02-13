package gui.dashboards;

import javax.swing.*;

import database.dao.UserDAO;
import core.entities.User;
import core.entities.Admin;
import core.entities.SuperAdmin;

import java.awt.*;
import java.awt.event.*;

import gui.auth.LoginPage;
import gui.components.employee.ManageProductsPanel;
import gui.components.employee.ManageUsersPanel;

public class EmployeeDashboard implements ActionListener {
    private UserDAO userDAO;
    private User user;
    private JFrame frame;
    private JPanel leftPanel, mainPanel;
    private JLabel welcomeLabel, shopNameLabel;
    private JButton dashButton, addUserButton, deleteUserButton, updateUserButton, listUsersButton, addProductButton,
            deleteProductButton, updateProductButton, listProductsButton, logoutButton;

    private ManageUsersPanel userPanel;
    private ManageProductsPanel productPanel;

    public EmployeeDashboard(String email) {
        userDAO = new UserDAO();
        user = userDAO.searchUser(email);

        frame = new JFrame("Dashboard");
        frame.setSize(980, 810);
        frame.setLayout(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        leftPanel = new JPanel();
        leftPanel.setBounds(0, 0, 240, 800);
        leftPanel.setBackground(Color.decode("#4B4B4B"));
        leftPanel.setLayout(null);

        mainPanel = new JPanel();
        mainPanel.setBounds(240, 0, 740, 800);
        mainPanel.setBackground(Color.decode("#041a42"));
        mainPanel.setLayout(null);

        frame.add(leftPanel);
        frame.add(mainPanel);

        if (user instanceof Admin || user instanceof SuperAdmin) {
            welcomeLabel = new JLabel("Welcome to the Admin Dashboard");
        } else {
            welcomeLabel = new JLabel("Welcome to the Employee Dashboard\n");
        }

        welcomeLabel.setForeground(new Color(219, 226, 233));
        welcomeLabel.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 30));
        welcomeLabel.setBounds(200, 300, 500, 50);
        mainPanel.add(welcomeLabel);

        shopNameLabel = new JLabel(user.getName(), JLabel.CENTER);
        shopNameLabel.setForeground(new Color(255, 140, 0));
        shopNameLabel.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 45));
        shopNameLabel.setBounds(250, 350, 300, 60);
        mainPanel.add(shopNameLabel);

        // Initialize buttons and add them to the left panel
        initializeButtons();

        // Initialize the panels
        userPanel = new ManageUsersPanel(mainPanel, user);
        productPanel = new ManageProductsPanel(mainPanel);

        mainPanel.revalidate();
        mainPanel.repaint();
        leftPanel.revalidate();
        leftPanel.repaint();
    }

    private void initializeButtons() {
        dashButton = createStyledButton("Dashboard", 5, true);
        addProductButton = createStyledButton("Add Product", 395, true);
        updateProductButton = createStyledButton("Update Product", 475, true);
        deleteProductButton = createStyledButton("Delete Product", 555, true);
        listProductsButton = createStyledButton("List Products", 635, true);
        logoutButton = createStyledButton("Log Out", 715, true);

        boolean buttonStatus = user instanceof Admin || user instanceof SuperAdmin;
        addUserButton = createStyledButton("Add User", 75, buttonStatus);
        updateUserButton = createStyledButton("Update User", 155, true);
        deleteUserButton = createStyledButton("Delete User", 235, buttonStatus);
        listUsersButton = createStyledButton("List Users", 315, buttonStatus);
        

        leftPanel.add(dashButton);
        leftPanel.add(addUserButton);
        leftPanel.add(updateUserButton);
        leftPanel.add(deleteUserButton);
        leftPanel.add(listUsersButton);
        leftPanel.add(addProductButton);
        leftPanel.add(updateProductButton);
        leftPanel.add(deleteProductButton);
        leftPanel.add(listProductsButton);
        leftPanel.add(logoutButton);
    }

    private JButton createStyledButton(String text, int yPosition, boolean isEnabled) {
        JButton button = new JButton(text);
        button.setBounds(20, yPosition, 200, 50);
        button.setBackground(new Color(255, 140, 0));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Verdana", Font.BOLD, 16));
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setEnabled(isEnabled);
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(255, 165, 0));
                button.repaint();
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(255, 140, 0));
                button.repaint();
            }
        });

        button.addActionListener(this);
        return button;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == dashButton) {
            showDashboard();
        } else if (e.getSource() == addUserButton) {
            userPanel.showAddUserPanel();
        } else if (e.getSource() == updateUserButton) {
            userPanel.showUpdateUserPanel();
        } else if (e.getSource() == deleteUserButton) {
            userPanel.showDeleteUserPanel();
        } else if (e.getSource() == listUsersButton) {
            userPanel.showListUsersPanel();
        } else if (e.getSource() == addProductButton) {
            productPanel.showAddProductPanel();
        } else if (e.getSource() == updateProductButton) {
            productPanel.showUpdateProductPanel();
        } else if (e.getSource() == deleteProductButton) {
            productPanel.showDeleteProductPanel();
        } else if (e.getSource() == listProductsButton) {
            productPanel.showListProductsPanel();
        } else if (e.getSource() == logoutButton) {
            frame.dispose();
            new LoginPage();
        }
    }

    public void showDashboard() {
        mainPanel.removeAll();
        welcomeLabel.setBounds(200, 300, 500, 50);
        shopNameLabel.setBounds(250, 350, 300, 60);
        mainPanel.add(welcomeLabel);
        mainPanel.add(shopNameLabel);
        mainPanel.revalidate();
        mainPanel.repaint();
    }
}
