package com.routefinder.ui;

import com.routefinder.MainApplication;
import com.routefinder.service.StationDataService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

public class InputPanel extends JPanel {

    private JComboBox<StationDataService.Line> lineComboStart;
    private JComboBox<StationDataService.Station> stationComboStart;

    private JComboBox<StationDataService.Line> lineComboEnd;
    private JComboBox<StationDataService.Station> stationComboEnd;

    public InputPanel(MainApplication app) {

        setBackground(new Color(245, 247, 250));
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 20, 12, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("MRT/BTS Fare Calculator");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(title, gbc);

        gbc.gridwidth = 1;

        // ================= START LINE =================
        gbc.gridy++;
        gbc.gridx = 0;
        add(label("Starting Line:"), gbc);

        gbc.gridx = 1;
        lineComboStart = new JComboBox<>();
        for (StationDataService.Line line : app.getDataService().getLines()) {
            lineComboStart.addItem(line);
        }
        styleCombo(lineComboStart, "Select starting line...");
        add(lineComboStart, gbc);

        // ================= START STATION =================
        gbc.gridy++;
        gbc.gridx = 0;
        add(label("Starting Station:"), gbc);

        gbc.gridx = 1;
        stationComboStart = new JComboBox<>();
        styleCombo(stationComboStart, "Select starting station...");
        add(stationComboStart, gbc);

        // ================= END LINE =================
        gbc.gridy++;
        gbc.gridx = 0;
        add(label("Destination Line:"), gbc);

        gbc.gridx = 1;
        lineComboEnd = new JComboBox<>();
        for (StationDataService.Line line : app.getDataService().getLines()) {
            lineComboEnd.addItem(line);
        }
        styleCombo(lineComboEnd, "Select destination line...");
        add(lineComboEnd, gbc);

        // ================= END STATION =================
        gbc.gridy++;
        gbc.gridx = 0;
        add(label("Destination Station:"), gbc);

        gbc.gridx = 1;
        stationComboEnd = new JComboBox<>();
        styleCombo(stationComboEnd, "Select destination station...");
        add(stationComboEnd, gbc);

        // Add Listeners
        lineComboStart.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                updateStations(stationComboStart, (StationDataService.Line) e.getItem(), app.getDataService());
            }
        });

        lineComboEnd.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                updateStations(stationComboEnd, (StationDataService.Line) e.getItem(), app.getDataService());
            }
        });

        // Initialize stations empty
        lineComboStart.setSelectedIndex(-1);
        lineComboEnd.setSelectedIndex(-1);
        // ================= BUTTONS =================
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(245, 247, 250));

        JButton clear = new JButton("Clear");
        JButton calc = new JButton("Calculate");

        styleButton(clear, new Color(200, 200, 200), Color.BLACK);
        styleButton(calc, new Color(41, 128, 185), Color.WHITE);

        calc.addActionListener(e -> app.showResult());
        clear.addActionListener(e -> {
            lineComboStart.setSelectedIndex(-1);
            lineComboEnd.setSelectedIndex(-1);
            stationComboStart.removeAllItems();
            stationComboEnd.removeAllItems();
        });

        buttonPanel.add(clear);
        buttonPanel.add(calc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);
    }

    // ================= UI HELPERS =================

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        l.setForeground(new Color(60, 60, 60));
        return l;
    }

    private void styleCombo(JComboBox<?> box, String hint) {
        box.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        box.setBackground(Color.WHITE);
        box.setPreferredSize(new Dimension(320, 35));

        box.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null && index == -1) {
                    setText(hint);
                    setForeground(Color.GRAY);
                } else {
                    setForeground(Color.BLACK);
                }
                return this;
            }
        });
    }

    private void updateStations(JComboBox<StationDataService.Station> combo, StationDataService.Line line,
            StationDataService service) {
        combo.removeAllItems();
        if (line != null) {
            for (StationDataService.Station s : service.getStationsByLine(line.code)) {
                combo.addItem(s);
            }
        }
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(140, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}