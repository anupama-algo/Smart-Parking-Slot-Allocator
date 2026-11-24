
package com.parking.ui;

import com.parking.dao.ParkingReportDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;

public class ReportsPanel extends JPanel {

    private final ParkingReportDAO reportDAO;

    private final JLabel totalSlotsLabel;
    private final JLabel occupiedSlotsLabel;
    private final JLabel availableSlotsLabel;
    private final JLabel collectionLabel;

    private final JTextArea areaReport;
    private final JTextArea categoryReport;
    private final JLabel statusLabel;

    private final Color TEXT = new Color(35, 45, 65);
    private final Color ACCENT = new Color(55, 110, 220);
    private final Color MUTED = new Color(115, 125, 145);

    public ReportsPanel() {

        reportDAO = new ParkingReportDAO();

        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 247, 251));
        setBorder(new EmptyBorder(20, 25, 20, 25));

        totalSlotsLabel = new JLabel("0");
        occupiedSlotsLabel = new JLabel("0");
        availableSlotsLabel = new JLabel("0");
        collectionLabel = new JLabel("Rs. 0.00");

        areaReport = createReportArea();
        categoryReport = createReportArea();

        statusLabel = new JLabel(" ");

        add(createHeader(), BorderLayout.NORTH);
        add(createReportContent(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);

        refreshReports();
    }

    private JPanel createHeader() {

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(
                new BoxLayout(heading, BoxLayout.Y_AXIS)
        );

        JLabel title = new JLabel("Parking Reports");

        title.setFont(
                new Font("SansSerif", Font.BOLD, 24)
        );
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel(
                "Monitor parking occupancy and daily revenue."
        );

        subtitle.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );
        subtitle.setForeground(MUTED);

        heading.add(title);
        heading.add(Box.createVerticalStrut(6));
        heading.add(subtitle);

        JButton refreshButton = new JButton("Refresh Reports");

        refreshButton.setFont(
                new Font("SansSerif", Font.BOLD, 12)
        );
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setBackground(ACCENT);
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );
        refreshButton.setBorder(
                new EmptyBorder(10, 15, 10, 15)
        );

        refreshButton.addActionListener(
                e -> refreshReports()
        );

        header.add(heading, BorderLayout.WEST);
        header.add(refreshButton, BorderLayout.EAST);

        return header;
    }

    private JPanel createReportContent() {

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(
                new BoxLayout(content, BoxLayout.Y_AXIS)
        );

        JPanel metrics = new JPanel(new GridLayout(1, 4, 12, 12));
        metrics.setOpaque(false);

        metrics.add(createMetricCard(
                "Total Slots", totalSlotsLabel
        ));

        metrics.add(createMetricCard(
                "Occupied Slots", occupiedSlotsLabel
        ));

        metrics.add(createMetricCard(
                "Available Slots", availableSlotsLabel
        ));

        metrics.add(createMetricCard(
                "Today's Collection", collectionLabel
        ));

        content.add(metrics);
        content.add(Box.createVerticalStrut(18));

        JPanel details = new JPanel(new GridLayout(1, 2, 15, 15));
        details.setOpaque(false);

        details.add(createReportSection(
                "Available Slots by Area",
                areaReport
        ));

        details.add(createReportSection(
                "Active Vehicles by Category",
                categoryReport
        ));

        content.add(details);

        return content;
    }

    private JPanel createMetricCard(
            String heading,
            JLabel valueLabel) {

        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(
                new BoxLayout(card, BoxLayout.Y_AXIS)
        );

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(225, 230, 238)
                        ),
                        new EmptyBorder(18, 15, 18, 15)
                )
        );

        JLabel title = new JLabel(heading);
        title.setFont(
                new Font("SansSerif", Font.PLAIN, 12)
        );
        title.setForeground(MUTED);

        valueLabel.setFont(
                new Font("SansSerif", Font.BOLD, 23)
        );
        valueLabel.setForeground(TEXT);

        card.add(title);
        card.add(Box.createVerticalStrut(12));
        card.add(valueLabel);

        return card;
    }

    private JPanel createReportSection(
            String heading,
            JTextArea reportArea) {

        JPanel section = new JPanel(new BorderLayout(10, 15));
        section.setBackground(Color.WHITE);

        section.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(225, 230, 238)
                        ),
                        new EmptyBorder(18, 18, 18, 18)
                )
        );

        JLabel title = new JLabel(heading);

        title.setFont(
                new Font("SansSerif", Font.BOLD, 15)
        );
        title.setForeground(TEXT);

        JScrollPane scrollPane =
                new JScrollPane(reportArea);

        scrollPane.setBorder(
                BorderFactory.createEmptyBorder()
        );

        section.add(title, BorderLayout.NORTH);
        section.add(scrollPane, BorderLayout.CENTER);

        return section;
    }

    private JTextArea createReportArea() {

        JTextArea area = new JTextArea();

        area.setEditable(false);
        area.setFont(
                new Font("SansSerif", Font.PLAIN, 14)
        );
        area.setForeground(
                new Color(65, 75, 95)
        );
        area.setBackground(Color.WHITE);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(
                new EmptyBorder(8, 2, 8, 2)
        );

        return area;
    }

    private JPanel createFooter() {

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);

        statusLabel.setFont(
                new Font("SansSerif", Font.PLAIN, 12)
        );
        statusLabel.setForeground(MUTED);

        footer.add(statusLabel, BorderLayout.WEST);

        return footer;
    }

    public void refreshReports() {

        try {

            // 1. Overall parking occupancy report.
            Map<String, Long> occupancy =
                    reportDAO.getOccupancyReport();

            totalSlotsLabel.setText(
                    String.valueOf(
                            occupancy.getOrDefault("Total Slots", 0L)
                    )
            );

            occupiedSlotsLabel.setText(
                    String.valueOf(
                            occupancy.getOrDefault("Occupied Slots", 0L)
                    )
            );

            availableSlotsLabel.setText(
                    String.valueOf(
                            occupancy.getOrDefault("Available Slots", 0L)
                    )
            );

            // 2. Available slots by parking area.
            Map<String, Long> areas =
                    reportDAO.getAvailableSlotsByArea();

            StringBuilder areaText = new StringBuilder();

            if (areas.isEmpty()) {

                areaText.append("No parking areas found.");

            } else {

                for (Map.Entry<String, Long> entry :
                        areas.entrySet()) {

                    areaText.append(entry.getKey())
                            .append("\nAvailable Slots: ")
                            .append(entry.getValue())
                            .append("\n\n");
                }
            }

            areaReport.setText(areaText.toString());

            // 3. Currently parked vehicles by category.
            Map<String, Long> categories =
                    reportDAO.getActiveVehiclesByCategory();

            StringBuilder categoryText = new StringBuilder();

            if (categories.isEmpty()) {

                categoryText.append(
                        "No vehicles are currently parked."
                );

            } else {

                for (Map.Entry<String, Long> entry :
                        categories.entrySet()) {

                    categoryText.append(entry.getKey())
                            .append(": ")
                            .append(entry.getValue())
                            .append(" vehicles\n\n");
                }
            }

            categoryReport.setText(categoryText.toString());

            // 4. Today's parking collection.
            BigDecimal collection =
                    reportDAO.getDailyCollection(
                            LocalDate.now()
                    );

            collectionLabel.setText(
                    "Rs. " + collection.toPlainString()
            );

            statusLabel.setText(
                    "Reports updated successfully."
            );

        } catch (SQLException e) {

            statusLabel.setText(
                    "Unable to load reports: " + e.getMessage()
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Could not load parking reports:\n"
                            + e.getMessage(),
                    "Report Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
