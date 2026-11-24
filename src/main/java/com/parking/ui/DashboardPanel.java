
package com.parking.ui;

import com.parking.dao.ParkingReportDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DashboardPanel extends JPanel {

    private final ParkingReportDAO reportDAO;
    private final JPanel dashboardContent;
    private final JScrollPane scrollPane;

    private static final Color BLUE = new Color(55, 125, 190);
    private static final Color ORANGE = new Color(220, 145, 55);
    private static final Color GREEN = new Color(45, 160, 110);
    private static final Color TEAL = new Color(0, 150, 145);

    public DashboardPanel() {

        reportDAO = new ParkingReportDAO();

        setLayout(new BorderLayout());
        setBackground(Theme.background());

        dashboardContent = new JPanel();
        dashboardContent.setLayout(
                new BoxLayout(dashboardContent, BoxLayout.Y_AXIS)
        );
        dashboardContent.setBackground(Theme.background());
        dashboardContent.setBorder(
                new EmptyBorder(10, 10, 25, 10)
        );

        scrollPane = new JScrollPane(dashboardContent);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Theme.background());
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);

        add(scrollPane, BorderLayout.CENTER);

        refreshDashboard();
    }

    public void applyTheme() {
        refreshDashboard();
    }

    public void refreshDashboard() {

        dashboardContent.removeAll();
        dashboardContent.setBackground(Theme.background());

        try {

            Map<String, Long> occupancy =
                    reportDAO.getOccupancyReport();

            long total = occupancy.getOrDefault("Total Slots", 0L);
            long occupied = occupancy.getOrDefault("Occupied Slots", 0L);
            long available = occupancy.getOrDefault("Available Slots", 0L);

            double occupancyRate = total == 0
                    ? 0
                    : (occupied * 100.0 / total);

            List<Map<String, Object>> areas =
                    reportDAO.getAreaWiseOccupancyReport();

            List<Map<String, Object>> operationalAreas =
                    new ArrayList<>();

            List<Map<String, Object>> testAreas =
                    new ArrayList<>();

            for (Map<String, Object> area : areas) {

                String name = String.valueOf(
                        area.getOrDefault("areaName", "")
                );

                if (name.equalsIgnoreCase("Integration Test Area")) {
                    testAreas.add(area);
                } else {
                    operationalAreas.add(area);
                }
            }

            addSectionHeading(
                    "Facility Snapshot",
                    "Live overview of parking capacity and usage."
            );

            JPanel metrics = new JPanel(
                    new GridLayout(1, 4, 14, 0)
            );
            metrics.setOpaque(false);
            metrics.setAlignmentX(Component.LEFT_ALIGNMENT);

            metrics.add(createMetricCard(
                    "TOTAL SLOTS", String.valueOf(total), BLUE
            ));

            metrics.add(createMetricCard(
                    "OCCUPIED", String.valueOf(occupied), ORANGE
            ));

            metrics.add(createMetricCard(
                    "AVAILABLE", String.valueOf(available), GREEN
            ));

            metrics.add(createMetricCard(
                    "OCCUPANCY RATE",
                    String.format("%.1f%%", occupancyRate),
                    TEAL
            ));

            dashboardContent.add(metrics);
            dashboardContent.add(Box.createVerticalStrut(30));

            addSectionHeading(
                    "Parking Area Performance",
                    "Occupancy and vehicle-category breakdown by location."
            );

            JPanel areaGrid = new JPanel(
                    new GridLayout(0, 2, 16, 16)
            );
            areaGrid.setOpaque(false);
            areaGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

            for (Map<String, Object> area : operationalAreas) {
                areaGrid.add(createAreaCard(area));
            }

            if (operationalAreas.isEmpty()) {
                areaGrid.add(createMessageCard(
                        "No operational parking areas found."
                ));
            }

            dashboardContent.add(areaGrid);

            if (!testAreas.isEmpty()) {

                dashboardContent.add(Box.createVerticalStrut(28));

                addSectionHeading(
                        "Testing Environment",
                        "Separate capacity reserved for integration testing."
                );

                JPanel testGrid = new JPanel(
                        new GridLayout(0, 2, 16, 16)
                );
                testGrid.setOpaque(false);
                testGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

                for (Map<String, Object> area : testAreas) {
                    testGrid.add(createAreaCard(area));
                }

                dashboardContent.add(testGrid);
            }

        } catch (Exception e) {

            dashboardContent.add(createMessageCard(
                    "Unable to load dashboard data: " + e.getMessage()
            ));

            e.printStackTrace();
        }

        scrollPane.getViewport().setBackground(Theme.background());

        dashboardContent.revalidate();
        dashboardContent.repaint();
        revalidate();
        repaint();
    }

    private void addSectionHeading(
            String heading,
            String description
    ) {

        JPanel section = new JPanel();
        section.setLayout(
                new BoxLayout(section, BoxLayout.Y_AXIS)
        );
        section.setOpaque(false);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel(heading);
        title.setFont(new Font("SansSerif", Font.BOLD, 19));
        title.setForeground(Theme.text());

        JLabel subtitle = new JLabel(description);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(Theme.muted());

        section.add(title);
        section.add(Box.createVerticalStrut(5));
        section.add(subtitle);
        section.add(Box.createVerticalStrut(15));

        dashboardContent.add(section);
    }

    private JPanel createMetricCard(
            String heading,
            String value,
            Color accent
    ) {

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Theme.surface());

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(
                        3, 0, 0, 0, accent
                ),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Theme.border()),
                        new EmptyBorder(17, 16, 17, 16)
                )
        ));

        JLabel headingLabel = new JLabel(heading);
        headingLabel.setFont(
                new Font("SansSerif", Font.BOLD, 10)
        );
        headingLabel.setForeground(Theme.muted());

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(
                new Font("SansSerif", Font.BOLD, 29)
        );
        valueLabel.setForeground(accent);

        card.add(headingLabel);
        card.add(Box.createVerticalStrut(12));
        card.add(valueLabel);

        return card;
    }

    private JPanel createAreaCard(
            Map<String, Object> area
    ) {

        String name = String.valueOf(
                area.getOrDefault("areaName", "Parking Area")
        );

        String location = String.valueOf(
                area.getOrDefault("location", "Location unavailable")
        );

        long total = getLong(area, "totalSlots");
        long occupied = getLong(area, "occupiedSlots");
        long available = getLong(area, "availableSlots");

        long totalCars = getLong(area, "totalCars");
        long occupiedCars = getLong(area, "occupiedCars");

        long totalBikes = getLong(area, "totalBikes");
        long occupiedBikes = getLong(area, "occupiedBikes");

        int percentage = total == 0
                ? 0
                : (int) Math.round(occupied * 100.0 / total);

        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(Theme.surface());

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.border()),
                new EmptyBorder(17, 18, 17, 18)
        ));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(
                new Font("SansSerif", Font.BOLD, 15)
        );
        nameLabel.setForeground(Theme.text());

        JLabel percentageLabel = new JLabel(percentage + "%");
        percentageLabel.setFont(
                new Font("SansSerif", Font.BOLD, 14)
        );
        percentageLabel.setForeground(TEAL);

        top.add(nameLabel, BorderLayout.WEST);
        top.add(percentageLabel, BorderLayout.EAST);

        JPanel details = new JPanel();
        details.setLayout(
                new BoxLayout(details, BoxLayout.Y_AXIS)
        );
        details.setOpaque(false);

        JLabel locationLabel = new JLabel(location);
        locationLabel.setFont(
                new Font("SansSerif", Font.PLAIN, 11)
        );
        locationLabel.setForeground(Theme.muted());

        JLabel slotSummary = new JLabel(
                occupied + " occupied  /  " + total + " total"
        );
        slotSummary.setFont(
                new Font("SansSerif", Font.BOLD, 12)
        );
        slotSummary.setForeground(Theme.text());

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(percentage);
        progressBar.setStringPainted(false);
        progressBar.setPreferredSize(new Dimension(100, 9));
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 9));
        progressBar.setForeground(TEAL);
        progressBar.setBackground(Theme.border());
        progressBar.setBorderPainted(false);

        JLabel carLabel = createCategoryLabel(
                "CAR", occupiedCars, totalCars
        );

        JLabel bikeLabel = createCategoryLabel(
                "BIKE", occupiedBikes, totalBikes
        );

        JLabel availableLabel = new JLabel(
                available + " slots available"
        );
        availableLabel.setFont(
                new Font("SansSerif", Font.BOLD, 11)
        );
        availableLabel.setForeground(GREEN);

        details.add(locationLabel);
        details.add(Box.createVerticalStrut(12));
        details.add(slotSummary);
        details.add(Box.createVerticalStrut(8));
        details.add(progressBar);
        details.add(Box.createVerticalStrut(13));
        details.add(carLabel);
        details.add(Box.createVerticalStrut(6));
        details.add(bikeLabel);
        details.add(Box.createVerticalStrut(10));
        details.add(availableLabel);

        card.add(top, BorderLayout.NORTH);
        card.add(details, BorderLayout.CENTER);

        return card;
    }

    private JLabel createCategoryLabel(
            String category,
            long occupied,
            long total
    ) {

        JLabel label = new JLabel(
                category + "   " + occupied + " occupied / " + total
        );

        label.setFont(
                new Font("SansSerif", Font.PLAIN, 11)
        );
        label.setForeground(Theme.muted());

        return label;
    }

    private JPanel createMessageCard(String message) {

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Theme.surface());
        card.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel label = new JLabel(message);
        label.setForeground(Theme.muted());

        card.add(label, BorderLayout.CENTER);

        return card;
    }

    private long getLong(
            Map<String, Object> data,
            String key
    ) {

        Object value = data.get(key);

        if (value instanceof Number number) {
            return number.longValue();
        }

        return 0;
    }
}
