package com.parking.ui;

import com.parking.dao.ParkingAreaDAO;
import com.parking.dao.ParkingSlotDAO;
import com.parking.model.ParkingArea;
import com.parking.model.ParkingSlot;
import com.parking.model.VehicleCategory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ParkingAreaPanel extends JPanel {

    private final ParkingAreaDAO areaDAO;
    private final ParkingSlotDAO slotDAO;

    private final JTextField areaCodeField;
    private final JTextField areaNameField;
    private final JTextField locationField;
    private final JTextField carSlotsField;
    private final JTextField bikeSlotsField;

    private DefaultTableModel tableModel;
    private JTable areaTable;

    private final Color TEXT = new Color(35, 45, 65);
    private final Color ACCENT = new Color(55, 110, 220);

    public ParkingAreaPanel() {

        areaDAO = new ParkingAreaDAO();
        slotDAO = new ParkingSlotDAO();

        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 247, 251));
        setBorder(new EmptyBorder(20, 25, 20, 25));

        areaCodeField = new JTextField(15);
        areaNameField = new JTextField(15);
        locationField = new JTextField(15);
        carSlotsField = new JTextField(15);
        bikeSlotsField = new JTextField(15);

        add(createHeader(), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 20));
        center.setOpaque(false);

        center.add(createForm(), BorderLayout.NORTH);
        center.add(createAreaTable(), BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);

        loadAreas();
    }

    private JPanel createHeader() {

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(
                new BoxLayout(header, BoxLayout.Y_AXIS)
        );

        JLabel title = new JLabel("Parking Area Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel(
                "Create parking areas and configure vehicle-specific slots."
        );
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(new Color(115, 125, 145));

        header.add(title);
        header.add(Box.createVerticalStrut(7));
        header.add(subtitle);

        return header;
    }

    private JPanel createForm() {

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(225, 230, 238)
                        ),
                        new EmptyBorder(20, 20, 20, 20)
                )
        );

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        addField(form, gbc, 0, 0, "Area Code", areaCodeField);
        addField(form, gbc, 1, 0, "Area Name", areaNameField);
        addField(form, gbc, 2, 0, "Location", locationField);
        addField(form, gbc, 0, 1, "Number of CAR Slots", carSlotsField);
        addField(form, gbc, 1, 1, "Number of BIKE Slots", bikeSlotsField);

        JButton createButton = new JButton("Create Parking Area");
        createButton.setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );
        createButton.setForeground(Color.WHITE);
        createButton.setBackground(ACCENT);
        createButton.setFocusPainted(false);
        createButton.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );
        createButton.setBorder(
                new EmptyBorder(12, 18, 12, 18)
        );

        createButton.addActionListener(e -> createParkingArea());

        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 0;
        form.add(createButton, gbc);

        return form;
    }

    private void addField(
            JPanel panel,
            GridBagConstraints gbc,
            int x,
            int y,
            String label,
            JTextField field) {

        JPanel fieldPanel = new JPanel();
        fieldPanel.setOpaque(false);
        fieldPanel.setLayout(
                new BoxLayout(fieldPanel, BoxLayout.Y_AXIS)
        );

        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(
                new Font("SansSerif", Font.BOLD, 12)
        );
        fieldLabel.setForeground(TEXT);

        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 35)
        );

        fieldPanel.add(fieldLabel);
        fieldPanel.add(Box.createVerticalStrut(7));
        fieldPanel.add(field);

        gbc.gridx = x;
        gbc.gridy = y;

        panel.add(fieldPanel, gbc);
    }

    private JPanel createAreaTable() {

        JPanel tablePanel = new JPanel(new BorderLayout(0, 12));
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(225, 230, 238)
                        ),
                        new EmptyBorder(18, 18, 18, 18)
                )
        );

        JLabel tableTitle = new JLabel("Registered Parking Areas");
        tableTitle.setFont(
                new Font("SansSerif", Font.BOLD, 17)
        );
        tableTitle.setForeground(TEXT);

        tableModel = new DefaultTableModel(
                new String[]{
                        "Area ID",
                        "Area Code",
                        "Area Name",
                        "Location",
                        "Total Slots"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        areaTable = new JTable(tableModel);
        areaTable.setRowHeight(30);
        areaTable.setFont(
                new Font("SansSerif", Font.PLAIN, 12)
        );
        areaTable.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12)
        );

        JScrollPane scrollPane = new JScrollPane(areaTable);

        tablePanel.add(tableTitle, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private void createParkingArea() {

        String areaCode = areaCodeField.getText().trim();
        String areaName = areaNameField.getText().trim();
        String location = locationField.getText().trim();

        if (areaCode.isEmpty() || areaName.isEmpty()) {
            showMessage(
                    "Area code and area name are required.",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int carCount;
        int bikeCount;

        try {
            carCount = Integer.parseInt(
                    carSlotsField.getText().trim()
            );

            bikeCount = Integer.parseInt(
                    bikeSlotsField.getText().trim()
            );

            if (carCount < 0 || bikeCount < 0
                    || carCount + bikeCount == 0) {
                throw new NumberFormatException();
            }

            if (carCount + bikeCount > 500) {
                showMessage(
                        "An area cannot contain more than 500 slots.",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

        } catch (NumberFormatException e) {
            showMessage(
                    "Enter valid non-negative slot counts. "
                            + "At least one slot is required.",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {

            ParkingArea area = new ParkingArea(
                    areaCode,
                    areaName,
                    location
            );

            long areaId = areaDAO.createArea(area);

            int slotNumber = 1;

            for (int i = 0; i < carCount; i++) {

                ParkingSlot slot = new ParkingSlot(
                        areaId,
                        slotNumber++,
                        VehicleCategory.CAR
                );

                slotDAO.createSlot(slot);
            }

            for (int i = 0; i < bikeCount; i++) {

                ParkingSlot slot = new ParkingSlot(
                        areaId,
                        slotNumber++,
                        VehicleCategory.BIKE
                );

                slotDAO.createSlot(slot);
            }

            showMessage(
                    "Parking area created successfully!\n"
                            + "Area Code: " + areaCode + "\n"
                            + "CAR Slots: " + carCount + "\n"
                            + "BIKE Slots: " + bikeCount,
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearFields();
            loadAreas();

        } catch (Exception e) {

            showMessage(
                    "Could not create parking area:\n"
                            + e.getMessage(),
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void loadAreas() {

        try {

            List<ParkingArea> areas = areaDAO.getAllAreas();

            tableModel.setRowCount(0);

            for (ParkingArea area : areas) {

                List<ParkingSlot> slots =
                        slotDAO.getSlotsByArea(area.getAreaId());

                tableModel.addRow(new Object[]{
                        area.getAreaId(),
                        area.getAreaCode(),
                        area.getAreaName(),
                        area.getLocation(),
                        slots.size()
                });
            }

        } catch (Exception e) {

            showMessage(
                    "Unable to load parking areas:\n"
                            + e.getMessage(),
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void clearFields() {

        areaCodeField.setText("");
        areaNameField.setText("");
        locationField.setText("");
        carSlotsField.setText("");
        bikeSlotsField.setText("");
    }

    private void showMessage(String message, int messageType) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "ParkSmart",
                messageType
        );
    }
}
