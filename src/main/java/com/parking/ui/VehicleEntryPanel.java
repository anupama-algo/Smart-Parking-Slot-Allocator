package com.parking.ui;

import com.parking.dao.ParkingAreaDAO;
import com.parking.dao.ParkingSessionDAO;
import com.parking.model.ParkingArea;
import com.parking.model.Vehicle;
import com.parking.model.VehicleCategory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class VehicleEntryPanel extends JPanel {

    private final ParkingAreaDAO areaDAO;
    private final ParkingSessionDAO sessionDAO;

    private final JTextField vehicleNumberField;
    private final JComboBox<VehicleCategory> categoryComboBox;
    private final JComboBox<ParkingArea> areaComboBox;
    private final JLabel statusLabel;

    private final Color TEXT = new Color(35, 45, 65);
    private final Color ACCENT = new Color(55, 110, 220);

    public VehicleEntryPanel() {

        areaDAO = new ParkingAreaDAO();
        sessionDAO = new ParkingSessionDAO();

        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 247, 251));
        setBorder(new EmptyBorder(25, 30, 25, 30));

        vehicleNumberField = new JTextField(20);
        categoryComboBox = new JComboBox<>(
                VehicleCategory.values()
        );
        areaComboBox = new JComboBox<>();
        statusLabel = new JLabel(" ");

        add(createHeader(), BorderLayout.NORTH);
        add(createEntryForm(), BorderLayout.CENTER);

        loadParkingAreas();
    }

    private JPanel createHeader() {

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(
                new BoxLayout(header, BoxLayout.Y_AXIS)
        );

        JLabel title = new JLabel("Vehicle Entry");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel(
                "Register vehicles and allocate available parking slots."
        );
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(new Color(115, 125, 145));

        header.add(title);
        header.add(Box.createVerticalStrut(7));
        header.add(subtitle);

        return header;
    }

    private JPanel createEntryForm() {

        JPanel container = new JPanel(new GridBagLayout());
        container.setOpaque(false);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(225, 230, 238)
                        ),
                        new EmptyBorder(30, 30, 30, 30)
                )
        );

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.WEST;

        addField(form, gbc, 0, "Vehicle Number",
                vehicleNumberField);

        addField(form, gbc, 1, "Vehicle Category",
                categoryComboBox);

        addField(form, gbc, 2, "Parking Area",
                areaComboBox);

        JButton entryButton =
                new JButton("Register Vehicle Entry");

        entryButton.setFont(
                new Font("SansSerif", Font.BOLD, 14)
        );
        entryButton.setForeground(Color.WHITE);
        entryButton.setBackground(ACCENT);
        entryButton.setFocusPainted(false);
        entryButton.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );
        entryButton.setBorder(
                new EmptyBorder(13, 20, 13, 20)
        );

        entryButton.addActionListener(
                e -> registerVehicleEntry()
        );

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        form.add(entryButton, gbc);

        statusLabel.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );
        statusLabel.setForeground(
                new Color(70, 85, 110)
        );

        gbc.gridy = 7;
        form.add(statusLabel, gbc);

        container.add(form);

        return container;
    }

    private void addField(
            JPanel panel,
            GridBagConstraints gbc,
            int row,
            String label,
            JComponent field) {

        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );
        fieldLabel.setForeground(TEXT);

        field.setFont(
                new Font("SansSerif", Font.PLAIN, 14)
        );

        gbc.gridx = 0;
        gbc.gridy = row * 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        panel.add(fieldLabel, gbc);

        gbc.gridy = row * 2 + 1;
        panel.add(field, gbc);
    }

    private void loadParkingAreas() {

        try {

            List<ParkingArea> areas =
                    areaDAO.getAllAreas();

            DefaultComboBoxModel<ParkingArea> model =
                    new DefaultComboBoxModel<>();

            for (ParkingArea area : areas) {
                model.addElement(area);
            }

            areaComboBox.setModel(model);

            areaComboBox.setRenderer(
                    new DefaultListCellRenderer() {

                        @Override
                        public Component getListCellRendererComponent(
                                JList<?> list,
                                Object value,
                                int index,
                                boolean isSelected,
                                boolean cellHasFocus) {

                            super.getListCellRendererComponent(
                                    list,
                                    value,
                                    index,
                                    isSelected,
                                    cellHasFocus
                            );

                            if (value instanceof ParkingArea area) {
                                setText(
                                        area.getAreaCode()
                                                + " - "
                                                + area.getAreaName()
                                );
                            }

                            return this;
                        }
                    }
            );

            if (areas.isEmpty()) {

                statusLabel.setText(
                        "No parking areas found. Create an area first."
                );

                areaComboBox.setEnabled(false);

            } else {

                areaComboBox.setEnabled(true);
            }

        } catch (Exception e) {

            statusLabel.setText(
                    "Unable to load parking areas: "
                            + e.getMessage()
            );
        }
    }

    private void registerVehicleEntry() {

        String vehicleNumber =
                vehicleNumberField.getText().trim();

        if (vehicleNumber.isEmpty()) {

            showMessage(
                    "Please enter the vehicle number.",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        ParkingArea selectedArea =
                (ParkingArea) areaComboBox.getSelectedItem();

        if (selectedArea == null) {

            showMessage(
                    "Please create or select a parking area first.",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        VehicleCategory category =
                (VehicleCategory) categoryComboBox.getSelectedItem();

        if (category == null) {

            showMessage(
                    "Please select a vehicle category.",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {

            Vehicle vehicle = new Vehicle(
                    vehicleNumber,
                    category
            );

            long sessionId = sessionDAO.recordEntry(
                    vehicle,
                    selectedArea.getAreaId()
            );

            showMessage(
                    "VEHICLE ENTRY SUCCESSFUL!\n\n"
                            + "Session ID: " + sessionId + "\n"
                            + "Vehicle Number: " + vehicleNumber + "\n"
                            + "Category: " + category + "\n"
                            + "Parking Area: "
                            + selectedArea.getAreaName(),
                    JOptionPane.INFORMATION_MESSAGE
            );

            vehicleNumberField.setText("");

            statusLabel.setText(
                    "Vehicle successfully registered."
            );

        } catch (SQLException | IllegalArgumentException e) {

            showMessage(
                    "Vehicle entry failed:\n" + e.getMessage(),
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (Exception e) {

            showMessage(
                    "Unexpected error:\n" + e.getMessage(),
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void showMessage(
            String message,
            int messageType) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "ParkSmart",
                messageType
        );
    }
}
