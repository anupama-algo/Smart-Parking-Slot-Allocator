package com.parking.ui;

import com.parking.dao.ParkingSessionDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;

public class VehicleExitPanel extends JPanel {

    private final ParkingSessionDAO sessionDAO;
    private final JTextField vehicleNumberField;
    private final JLabel statusLabel;

    private final Color TEXT = new Color(35, 45, 65);
    private final Color ACCENT = new Color(55, 110, 220);

    public VehicleExitPanel() {

        sessionDAO = new ParkingSessionDAO();

        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 247, 251));
        setBorder(new EmptyBorder(25, 30, 25, 30));

        vehicleNumberField = new JTextField(20);
        statusLabel = new JLabel(" ");

        add(createHeader(), BorderLayout.NORTH);
        add(createExitForm(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(
                new BoxLayout(header, BoxLayout.Y_AXIS)
        );

        JLabel title = new JLabel("Vehicle Exit");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel(
                "Process vehicle exits, calculate parking fees and release slots."
        );
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(new Color(115, 125, 145));

        header.add(title);
        header.add(Box.createVerticalStrut(7));
        header.add(subtitle);

        return header;
    }

    private JPanel createExitForm() {

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

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel label = new JLabel("Vehicle Number");
        label.setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );
        label.setForeground(TEXT);

        form.add(label, gbc);

        gbc.gridy = 1;

        vehicleNumberField.setFont(
                new Font("SansSerif", Font.PLAIN, 14)
        );

        form.add(vehicleNumberField, gbc);

        JButton exitButton = new JButton("Process Vehicle Exit");

        exitButton.setFont(
                new Font("SansSerif", Font.BOLD, 14)
        );
        exitButton.setForeground(Color.WHITE);
        exitButton.setBackground(ACCENT);
        exitButton.setFocusPainted(false);
        exitButton.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );
        exitButton.setBorder(
                new EmptyBorder(13, 20, 13, 20)
        );

        exitButton.addActionListener(
                e -> processVehicleExit()
        );

        gbc.gridy = 2;

        form.add(exitButton, gbc);

        statusLabel.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );
        statusLabel.setForeground(
                new Color(70, 85, 110)
        );

        gbc.gridy = 3;

        form.add(statusLabel, gbc);

        container.add(form);

        return container;
    }

    private void processVehicleExit() {

        String vehicleNumber =
                vehicleNumberField.getText().trim();

        if (vehicleNumber.isEmpty()) {

            showMessage(
                    "Please enter the vehicle number.",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        try {

            BigDecimal parkingFee =
                    sessionDAO.recordExit(vehicleNumber);

            showMessage(
                    "VEHICLE EXIT SUCCESSFUL!\n\n"
                            + "Vehicle Number: " + vehicleNumber + "\n"
                            + "Parking Fee: Rs. "
                            + parkingFee.toPlainString() + "\n\n"
                            + "Parking slot has been released.",
                    JOptionPane.INFORMATION_MESSAGE
            );

            vehicleNumberField.setText("");

            statusLabel.setText(
                    "Vehicle exit completed. Slot released successfully."
            );

        } catch (SQLException e) {

            showMessage(
                    "Vehicle exit failed:\n" + e.getMessage(),
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