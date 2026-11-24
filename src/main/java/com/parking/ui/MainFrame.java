package com.parking.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainFrame extends JFrame {

    private final CardLayout cardLayout;
    private final JPanel contentPanel;

    private final JPanel sidebar;
    private final JPanel mainArea;
    private final JPanel header;

    private final JLabel pageTitle;
    private final JLabel pageDescription;

    private final JButton themeButton;
    private final Map<String, JButton> navigationButtons =
            new LinkedHashMap<>();

    private String currentPage = "Dashboard";

    public MainFrame() {

        setTitle("Smart Parking Slot Allocator");
        setSize(1200, 760);
        setMinimumSize(new Dimension(1000, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        sidebar = new JPanel();
        mainArea = new JPanel(new BorderLayout());
        header = new JPanel(new BorderLayout());

        pageTitle = new JLabel();
        pageDescription = new JLabel();

        themeButton = new JButton();

        createContentPages();

        setLayout(new BorderLayout());
        add(createSidebar(), BorderLayout.WEST);
        add(createMainArea(), BorderLayout.CENTER);

        showPage("Dashboard");
        applyTheme();
    }

    private JPanel createSidebar() {

        sidebar.setPreferredSize(new Dimension(245, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(
                new EmptyBorder(30, 18, 22, 18)
        );

        JLabel brand = new JLabel("PARKSMART");
        brand.setFont(new Font("SansSerif", Font.BOLD, 25));
        brand.setForeground(Color.WHITE);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("SMART PARKING MANAGEMENT");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 10));
        subtitle.setForeground(new Color(158, 178, 193));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(brand);
        sidebar.add(Box.createVerticalStrut(7));
        sidebar.add(subtitle);
        sidebar.add(Box.createVerticalStrut(45));

        JLabel menuLabel = new JLabel("WORKSPACE");
        menuLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        menuLabel.setForeground(new Color(130, 153, 171));
        menuLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(menuLabel);
        sidebar.add(Box.createVerticalStrut(14));

        String[] pages = {
                "Dashboard",
                "Parking Areas",
                "Vehicle Entry",
                "Vehicle Exit",
                "Reports"
        };

        for (String page : pages) {

            JButton button = new JButton(page);

            button.setMaximumSize(
                    new Dimension(Integer.MAX_VALUE, 48)
            );

            button.setPreferredSize(new Dimension(205, 48));
            button.setAlignmentX(Component.LEFT_ALIGNMENT);
            button.setHorizontalAlignment(SwingConstants.LEFT);
            button.setFont(new Font("SansSerif", Font.BOLD, 13));
            button.setForeground(new Color(210, 222, 231));
            button.setBackground(new Color(17, 35, 49));
            button.setBorder(
                    new EmptyBorder(12, 17, 12, 10)
            );
            button.setFocusPainted(false);
            button.setContentAreaFilled(true);
            button.setOpaque(true);
            button.setBorderPainted(false);
            button.setCursor(
                    new Cursor(Cursor.HAND_CURSOR)
            );

            button.addActionListener(e -> showPage(page));

            navigationButtons.put(page, button);

            sidebar.add(button);
            sidebar.add(Box.createVerticalStrut(9));
        }

        sidebar.add(Box.createVerticalGlue());

        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(55, 75, 90));
        separator.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 1)
        );

        sidebar.add(separator);
        sidebar.add(Box.createVerticalStrut(15));

        JLabel footer = new JLabel("JAVA PROJECT  |  2026");
        footer.setFont(new Font("SansSerif", Font.PLAIN, 11));
        footer.setForeground(new Color(145, 165, 180));
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(footer);

        return sidebar;
    }

    private JPanel createMainArea() {

        mainArea.setBorder(
                new EmptyBorder(27, 32, 25, 32)
        );

        header.setOpaque(false);
        header.setBorder(
                new EmptyBorder(0, 0, 25, 0)
        );

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(
                new BoxLayout(heading, BoxLayout.Y_AXIS)
        );

        pageTitle.setFont(
                new Font("SansSerif", Font.BOLD, 27)
        );

        pageDescription.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );

        heading.add(pageTitle);
        heading.add(Box.createVerticalStrut(7));
        heading.add(pageDescription);

        themeButton.setFont(
                new Font("SansSerif", Font.BOLD, 12)
        );

        themeButton.setPreferredSize(new Dimension(125, 40));
        themeButton.setFocusPainted(false);
        themeButton.setBorderPainted(false);
        themeButton.setContentAreaFilled(true);
        themeButton.setOpaque(true);
        themeButton.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        themeButton.addActionListener(e -> {
            Theme.toggle();
            applyTheme();
        });

        header.add(heading, BorderLayout.WEST);
        header.add(themeButton, BorderLayout.EAST);

        mainArea.add(header, BorderLayout.NORTH);
        mainArea.add(contentPanel, BorderLayout.CENTER);

        return mainArea;
    }

    private void createContentPages() {

        contentPanel.add(
                new DashboardPanel(),
                "Dashboard"
        );

        contentPanel.add(
                new ParkingAreaPanel(),
                "Parking Areas"
        );

        contentPanel.add(
                new VehicleEntryPanel(),
                "Vehicle Entry"
        );

        contentPanel.add(
                new VehicleExitPanel(),
                "Vehicle Exit"
        );

        contentPanel.add(
                new ReportsPanel(),
                "Reports"
        );
    }

    private void showPage(String page) {

        currentPage = page;
        cardLayout.show(contentPanel, page);

        switch (page) {

            case "Dashboard" -> {
                pageTitle.setText("Dashboard");
                pageDescription.setText(
                        "Your parking operations at a glance."
                );
            }

            case "Parking Areas" -> {
                pageTitle.setText("Parking Areas");
                pageDescription.setText(
                        "Create and manage parking locations and slots."
                );
            }

            case "Vehicle Entry" -> {
                pageTitle.setText("Vehicle Entry");
                pageDescription.setText(
                        "Register incoming vehicles and allocate parking slots."
                );
            }

            case "Vehicle Exit" -> {
                pageTitle.setText("Vehicle Exit");
                pageDescription.setText(
                        "Process departures and calculate parking fees."
                );
            }

            case "Reports" -> {
                pageTitle.setText("Reports & Analytics");
                pageDescription.setText(
                        "Monitor occupancy, availability and daily collection."
                );
            }
        }

        updateNavigation();

        for (Component component : contentPanel.getComponents()) {

            if ("Dashboard".equals(page)
                    && component instanceof DashboardPanel dashboard) {

                dashboard.refreshDashboard();
                break;
            }

            if ("Parking Areas".equals(page)
                    && component instanceof ParkingAreaPanel parkingPanel) {

                parkingPanel.loadAreas();
                break;
            }

            if ("Reports".equals(page)
                    && component instanceof ReportsPanel reportsPanel) {

                reportsPanel.refreshReports();
                break;
            }
        }
    }

    private void updateNavigation() {

        for (Map.Entry<String, JButton> entry :
                navigationButtons.entrySet()) {

            JButton button = entry.getValue();
            boolean selected = entry.getKey().equals(currentPage);

            button.setBackground(
                    selected
                            ? Theme.ACCENT
                            : Theme.sidebar()
            );

            button.setForeground(
                    selected
                            ? Color.WHITE
                            : new Color(210, 222, 231)
            );
        }
    }

    private void applyTheme() {

        getContentPane().setBackground(Theme.background());

        sidebar.setBackground(Theme.sidebar());
        mainArea.setBackground(Theme.background());
        contentPanel.setBackground(Theme.background());

        pageTitle.setForeground(Theme.text());
        pageDescription.setForeground(Theme.muted());

        themeButton.setText(
                Theme.isDarkMode()
                        ? "☀  Light Mode"
                        : "☾  Dark Mode"
        );

        themeButton.setBackground(Theme.surface());
        themeButton.setForeground(Theme.text());

        themeButton.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Theme.border()),
                        new EmptyBorder(5, 12, 5, 12)
                )
        );

        updateNavigation();

        applyComponentTheme(contentPanel);

        revalidate();
        repaint();
    }

    private void applyComponentTheme(Component component) {

        if (component instanceof DashboardPanel dashboard) {
                dashboard.applyTheme();
                return;
        }

        if (component instanceof JPanel panel) {
            panel.setBackground(Theme.background());
        }

        if (component instanceof JLabel label) {
            label.setForeground(Theme.text());
        }

        if (component instanceof JButton button) {
            button.setBackground(Theme.ACCENT);
            button.setForeground(Color.WHITE);
            button.setOpaque(true);
            button.setContentAreaFilled(true);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
        }

        if (component instanceof JTextField field) {
            field.setBackground(Theme.surface());
            field.setForeground(Theme.text());
            field.setCaretColor(Theme.text());
        }

        if (component instanceof JTextArea area) {
            area.setBackground(Theme.surface());
            area.setForeground(Theme.text());
            area.setCaretColor(Theme.text());
        }

        if (component instanceof JComboBox<?> comboBox) {
            comboBox.setBackground(Theme.surface());
            comboBox.setForeground(Theme.text());
        }

        if (component instanceof JTable table) {
            table.setBackground(Theme.surface());
            table.setForeground(Theme.text());
            table.setGridColor(Theme.border());
            table.setSelectionBackground(Theme.ACCENT);
            table.setSelectionForeground(Color.WHITE);

            table.getTableHeader().setBackground(Theme.sidebar());
            table.getTableHeader().setForeground(Color.WHITE);
        }

        if (component instanceof JScrollPane scrollPane) {
            scrollPane.getViewport().setBackground(Theme.surface());
            scrollPane.setBorder(
                    BorderFactory.createLineBorder(Theme.border())
            );
        }

        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                applyComponentTheme(child);
            }
        }
    }
}
