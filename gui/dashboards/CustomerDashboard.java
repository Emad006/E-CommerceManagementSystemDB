package gui.dashboards;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import core.entities.Product;
import core.entities.Customer;
import database.dao.ProductDAO;
import database.dao.UserDAO;
import gui.auth.LoginPage;
import gui.components.customer.CartFrame;
import gui.components.customer.CustomerEditFrame;
import gui.components.customer.ProductPanel;

public class CustomerDashboard extends JFrame {
    private ProductDAO productDAO;
    private UserDAO userDAO;
    private ArrayList<Product> productList;
    private Customer customer;
    private JPanel productSpace; // productSpace is declared here to be accessible in the search logic

    public CustomerDashboard(String customerEmail) {
        productDAO = new ProductDAO();
        userDAO = new UserDAO();

        // Initialize the product list and product panels
        productList = productDAO.getAllProducts();
        // this.customerEmail = customerEmail;
        updateCustomerObject(customerEmail);

        // Set basic JFrame properties
        setTitle("Customer Dashboard");
        setSize(1420, 1024);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // North Panel (Top panel containing logo, text, search bar, icons)
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(Color.decode("#a2ba07")); // Background color

        // Left side: App logo and text ("E-Cart")
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(Color.decode("#a2ba07"));
        JLabel titleIcon = new JLabel(new ImageIcon("../assets/images/authAssets/shoppingCart.png"));
        JLabel titleLabel = new JLabel("E-Cart");
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
        leftPanel.add(titleIcon);
        leftPanel.add(titleLabel);

        // Center: Search bar with search icon
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        searchPanel.setBackground(Color.decode("#a2ba07"));
        JLabel searchIcon = new JLabel(new ImageIcon("../assets/images/authAssets/magnifyingGlass.png"));
        searchIcon.setBackground(Color.decode("#7a8928"));
        // Setting focus on search icon cause the default focus is on the search bar.
        SwingUtilities.invokeLater(() -> searchIcon.requestFocusInWindow());

        JTextField searchBar = new JTextField();
        searchBar.setForeground(Color.GRAY);
        searchBar.setText("Search for products or categories...");
        searchBar.setPreferredSize(new Dimension(400, 30)); // Adjust to center

        // Logic for search bar placeholder text
        searchBar.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                searchBar.setText("");
                searchBar.setForeground(Color.BLACK);
            }

            public void focusLost(FocusEvent e) {
                if (searchBar.getText().isEmpty()) {
                    searchBar.setForeground(Color.GRAY);
                    searchBar.setText("Search for products or categories...");
                }
            }
        });

        // Logic for searching when Enter key is pressed
        searchBar.addActionListener(e -> performSearch(searchBar.getText()));

        // Logic for searching when icon is clicked
        searchIcon.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                performSearch(searchBar.getText());
            }

            public void mouseEntered(MouseEvent e) {
                searchIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        });

        searchPanel.add(searchIcon);
        searchPanel.add(searchBar);

        // Right side: Profile, cart, and logout icons
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 5));
        rightPanel.setBackground(Color.decode("#a2ba07"));
        JLabel profileIcon = new JLabel(new ImageIcon("../assets/images/authAssets/user.png"));
        profileIcon.setToolTipText("View & Edit your Profile");
        profileIcon.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                updateCustomerObject(customerEmail);
                new CustomerEditFrame(customer);
            }

            public void mouseEntered(MouseEvent e) {
                profileIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        });
        JLabel cartIcon = new JLabel(new ImageIcon("../assets/images/authAssets/groceryStore.png"));
        cartIcon.setToolTipText("View your Cart");
        cartIcon.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new CartFrame(customer);
            }

            public void mouseEntered(MouseEvent e) {
                cartIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        });

        JLabel logoutIcon = new JLabel(new ImageIcon("../assets/images/authAssets/logOut.png"));
        logoutIcon.setToolTipText("Logout");
        logoutIcon.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                dispose();
                new LoginPage();
            }

            public void mouseEntered(MouseEvent e) {
                logoutIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        });

        rightPanel.add(cartIcon);
        rightPanel.add(profileIcon);
        rightPanel.add(logoutIcon);

        // Add left, center, and right panels to the northPanel
        northPanel.add(leftPanel, BorderLayout.WEST);
        northPanel.add(searchPanel, BorderLayout.CENTER);
        northPanel.add(rightPanel, BorderLayout.EAST);

        // Add the northPanel to the frame
        add(northPanel, BorderLayout.NORTH);

        // West Panel (Sidebar)
        JPanel westPanel = new JPanel();
        westPanel.setBackground(Color.decode("#041a42"));
        westPanel.setLayout(new BoxLayout(westPanel, BoxLayout.Y_AXIS));

        JComboBox<String> categoryComboBox = new JComboBox<>(productDAO.getAllCategories());
        categoryComboBox.setBackground(Color.decode("#041a42"));
        categoryComboBox.setForeground(Color.WHITE);
        categoryComboBox.setBounds(0, 290, 120, 30);
        categoryComboBox.addActionListener(e -> performCategorySearch(categoryComboBox.getSelectedItem().toString()));
        // add(categoryComboBox);

        String[] priceRanges = { "Price Range", "$0-$100", "$100-$500", "$500-$1,000", "$1,000-$5,000",
                "$5,000-$10,000", "$10,000+" };
        JComboBox<String> priceComboBox = new JComboBox<>(priceRanges);
        priceComboBox.setBackground(Color.decode("#041a42"));
        priceComboBox.setForeground(Color.WHITE);
        priceComboBox.setBounds(0, 330, 120, 30);

        priceComboBox.addActionListener(e -> {
            double lowerBound = 0;
            double upperBound = Double.MAX_VALUE;

            if (priceComboBox.getSelectedItem().toString().equals("$0-$100")) {
                lowerBound = 0;
                upperBound = 100;
            } else if (priceComboBox.getSelectedItem().toString().equals("$100-$500")) {
                lowerBound = 100;
                upperBound = 500;
            } else if (priceComboBox.getSelectedItem().toString().equals("$500-$1,000")) {
                lowerBound = 500;
                upperBound = 1000;
            } else if (priceComboBox.getSelectedItem().toString().equals("$1,000-$5,000")) {
                lowerBound = 1000;
                upperBound = 5000;
            } else if (priceComboBox.getSelectedItem().toString().equals("$5,000-$10,000")) {
                lowerBound = 5000;
                upperBound = 10000;
            } else if (priceComboBox.getSelectedItem().toString().equals("$10,000+")) {
                lowerBound = 10000;
                upperBound = Double.MAX_VALUE;
            }
            priceRangeSearch(lowerBound, upperBound);
        });

        // add(priceComboBox);

        // Create sidebar buttons styled as in the image
        JButton dashboardButton = createSidebarButton("Dashboard");
        JButton filtersButton = createSidebarButton("Filters");

        JLabel backIcon = new JLabel(new ImageIcon("../assets/images/authAssets/backButton.png"));
        backIcon.setBounds(28, 540, 60, 35);
        backIcon.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                dispose();
                new LoginPage();
            }

            public void mouseEntered(MouseEvent e) {
                backIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        });
        add(backIcon);

        // Add buttons to the westPanel
        westPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Top padding
        westPanel.add(dashboardButton);

        westPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        westPanel.add(filtersButton);

        westPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        westPanel.add(priceComboBox);

        westPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        westPanel.add(categoryComboBox);

        westPanel.add(Box.createRigidArea(new Dimension(0, 800))); // Bottom padding

        // Add westPanel to the frame
        add(westPanel, BorderLayout.WEST);

        // Centre Panel with ScrollPane (Keeping previous configuration intact)
        // The bannerPanel and productSpace remain unchanged.
        JPanel centrePanel = new JPanel(new BorderLayout());

        // Banner Panel (Top of centrePanel)
        JPanel bannerPanel = new JPanel(null); // Using null layout for manual positioning
        bannerPanel.setPreferredSize(new Dimension(1250, 900));
        bannerPanel.setBackground(Color.decode("#0c0b10"));

        // Text for bannerPanel
        JLabel bannerText = new JLabel(
                "<html><h1 style='color:#c9c6c1'>Welcome to Your E-Cart Dashboard, " + customer.getName()
                        + "!</h1><p>Discover the latest tech at unbeatable prices.</p><p>We bring you the best in cutting-edge technology. Browse our extensive catalog, view detailed product information, and experience a seamless shopping experience.</p></html>");
        Font bannerFont = new Font("open sans", Font.BOLD, 18);
        bannerText.setFont(bannerFont);
        bannerText.setForeground(Color.decode("#D3D3D3"));
        bannerText.setBounds(750, 210, 390, 280); // Manually setting bounds

        // Image for bannerPanel
        JLabel bannerImage = new JLabel(new ImageIcon("../assets/images/authAssets/banner.jpg"));
        bannerImage.setBounds(0, 0, 735, 670); // Manually setting bounds for image

        // Horizontal Panel inside bannerPanel
        JPanel horizontalPanel = new JPanel();
        horizontalPanel.setLayout(new GridLayout(1, 3, 20, 0)); // 3 columns, horizontal layout with spacing
        horizontalPanel.setBounds(10, 670, 1200, 250); // Setting position and size for horizontal panel
        horizontalPanel.setBackground(Color.decode("#050507")); // Background color for horizontal panel

        // Create a panel for "Exclusive Deals" with heading, icon, and text
        JPanel exclusiveDealsPanel = new JPanel();
        exclusiveDealsPanel.setLayout(new BoxLayout(exclusiveDealsPanel, BoxLayout.Y_AXIS));
        exclusiveDealsPanel.setBackground(Color.decode("#0c0b10")); // Match background color

        // Add an icon to the label
        ImageIcon dealsIcon = new ImageIcon("../assets/images/authAssets/financialDeal.png");
        JLabel dealsLabel = new JLabel("<html><h2 style='color:#ff881e'>Exclusive Deals</h2></html>", dealsIcon,
                SwingConstants.CENTER);
        Font dealsHeadingFont = new Font("Georgia", Font.BOLD, 18);
        dealsLabel.setFont(dealsHeadingFont);
        dealsLabel.setHorizontalTextPosition(SwingConstants.CENTER); // Center the text below the icon
        dealsLabel.setVerticalTextPosition(SwingConstants.BOTTOM); // Position text below the icon
        dealsLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the label

        exclusiveDealsPanel.add(dealsLabel);

        // Add the descriptive text for "Exclusive Deals"
        JLabel dealsText = new JLabel(
                "<html><h2 style='color:#c9c6c1'>Unlock unbeatable savings with our daily exclusive deals! Discover limited-time offers, flash sales, and discounts on top-rated products across all categories.</h2></html>",
                SwingConstants.CENTER);
        dealsText.setBounds(westPanel.getWidth() + 10, dealsText.getY(), dealsText.getWidth(), dealsText.getHeight()); // Add
                                                                                                                       // 10
                                                                                                                       // pixels
                                                                                                                       // of
                                                                                                                       // padding

        Font dealsFont = new Font("open sans", Font.PLAIN, 18);
        dealsText.setFont(dealsFont);
        dealsText.setAlignmentX(Component.CENTER_ALIGNMENT); // Align to center
        exclusiveDealsPanel.add(dealsText);

        // Create a panel for "Safe and Secure Payment" with heading, icon, and text
        JPanel securePaymentPanel = new JPanel();
        securePaymentPanel.setLayout(new BoxLayout(securePaymentPanel, BoxLayout.Y_AXIS));
        securePaymentPanel.setBackground(Color.decode("#0c0b10")); // Match background color

        // Add an icon to the label
        ImageIcon secureIcon = new ImageIcon("../assets/images/authAssets/creditCard.png");
        JLabel reviewsLabel = new JLabel("<html><h2 style='color:#ff881e'>Safe and Secure Payment</h2></html>",
                secureIcon, SwingConstants.CENTER);
        Font reviewFont = new Font("Georgia", Font.BOLD, 18);
        reviewsLabel.setFont(reviewFont);
        reviewsLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        reviewsLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
        reviewsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        securePaymentPanel.add(reviewsLabel);

        // Add the descriptive text for "Safe and Secure Payment"
        JLabel securePaymentText = new JLabel(
                "<html><h2 style='color:#c9c6c1'>Shop confidently with our trusted and secure payment methods. We safeguard your personal information </h2></html>",
                SwingConstants.CENTER);
        securePaymentText.setAlignmentX(Component.CENTER_ALIGNMENT); // Align to center
        securePaymentPanel.add(securePaymentText);

        // Create a panel for "24/7 Customer Support" with heading, icon, and text
        JPanel customerSupportPanel = new JPanel();
        customerSupportPanel.setLayout(new BoxLayout(customerSupportPanel, BoxLayout.Y_AXIS));
        customerSupportPanel.setBackground(Color.decode("#0c0b10")); // Match background color

        // Add an icon to the label
        ImageIcon supportIcon = new ImageIcon("../assets/images/authAssets/customerService.png");
        JLabel customerSupportLabel = new JLabel("<html><h2 style='color:#ff881e'>24/7 Customer Support</h2></html>",
                supportIcon, SwingConstants.CENTER);
        Font customerSupportFont = new Font("Georgia", Font.BOLD, 18);
        customerSupportLabel.setFont(customerSupportFont);
        customerSupportLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        customerSupportLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
        customerSupportLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        customerSupportPanel.add(customerSupportLabel);

        // Add the descriptive text for "24/7 Customer Support"
        JLabel customerSupportText = new JLabel(
                "<html><h2 style='color:#c9c6c1'>We're here to assist you anytime, any day!</h2></html>",
                SwingConstants.CENTER);
        customerSupportText.setAlignmentX(Component.CENTER_ALIGNMENT); // Align to center
        customerSupportPanel.add(customerSupportText);

        // Adding panels to the horizontalPanel
        horizontalPanel.add(exclusiveDealsPanel);
        horizontalPanel.add(securePaymentPanel);
        horizontalPanel.add(customerSupportPanel);

        // Adding components to bannerPanel
        bannerPanel.add(horizontalPanel); // Adding the new horizontal panel

        // Add text and image to bannerPanel
        bannerPanel.add(bannerText);
        bannerPanel.add(bannerImage);

        // Product Space (Bottom of centrePanel)
        productSpace = new JPanel();
        productSpace.setLayout(new BoxLayout(productSpace, BoxLayout.Y_AXIS)); // Vertically stack products
        productSpace.setBackground(Color.LIGHT_GRAY);

        for (Product product : productList) {
            ProductPanel productPanel = new ProductPanel(customer, product);
            productSpace.add(productPanel);
        }

        // Add bannerPanel and productSpace to centrePanel
        centrePanel.add(bannerPanel, BorderLayout.NORTH);
        centrePanel.add(productSpace, BorderLayout.CENTER);

        // Wrap the centrePanel in a JScrollPane
        JScrollPane scrollPane = new JScrollPane(centrePanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Add the scrollPane to the main frame's centre
        add(scrollPane, BorderLayout.CENTER);

        // Set the frame visible
        setVisible(true);
    }

    // Method to style sidebar buttons
    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false); // Remove focus outline
        button.setForeground(Color.WHITE);
        button.setBackground(Color.decode("#041a42"));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding
        button.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align in BoxLayout
        return button;
    }

    // Update the Customer object
    private void updateCustomerObject(String customerEmail) {
        customer = (Customer) userDAO.searchUser(customerEmail);
    }

    private void performSearch(String query) {
        productSpace.removeAll();
        productSpace.revalidate();
        productSpace.repaint();

        for (Product p : productList) {
            if (p.getName().toLowerCase().contains(query.toLowerCase())
                    || p.getDescription().toLowerCase().contains(query.toLowerCase())) {
                ProductPanel productPanel = new ProductPanel(customer, p);
                productSpace.add(productPanel);
            }
        }
    }

    private void performCategorySearch(String category) {
        productSpace.removeAll();
        productSpace.revalidate();
        productSpace.repaint();

        if (category.equals("Category")) {
            for (Product p : productList) {
                ProductPanel productPanel = new ProductPanel(customer, p);
                productSpace.add(productPanel);
            }
            return;
        }

        for (Product p : productList) {
            if (p.getCategory().equalsIgnoreCase(category)) {
                ProductPanel productPanel = new ProductPanel(customer, p);
                productSpace.add(productPanel);
            }
        }
    }

    private void priceRangeSearch(double lowerBound, double upperBound) {
        productSpace.removeAll();
        productSpace.revalidate();
        productSpace.repaint();

        for (Product p : productList) {
            if (p.getPrice() >= lowerBound && p.getPrice() <= upperBound) {
                ProductPanel productPanel = new ProductPanel(customer, p);
                productSpace.add(productPanel);
            }
        }
    }
}
