package com.routefinder.view;

import com.routefinder.controller.RouteController;
import com.routefinder.model.Line;
import com.routefinder.model.Station;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

public class InputPanel extends JPanel {

    private JComboBox<Line> lineComboStart;
    private JComboBox<Station> stationComboStart;

    private JComboBox<Line> lineComboEnd;
    private JComboBox<Station> stationComboEnd;

    public InputPanel(RouteController controller) {

        setBackground(new Color(245, 247, 250));
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 20, 12, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel thaiTitle = new JLabel("โปรแกรมค้นหาเส้นทางรถไฟฟ้าไทย");
        thaiTitle.setFont(new Font("Tahoma", Font.BOLD, 26));
        thaiTitle.setHorizontalAlignment(SwingConstants.CENTER);

        // JLabel engTitle = new JLabel("Thai Mass Transit Fare Route Finder");
        // engTitle.setFont(new Font("Tahoma", Font.BOLD, 26));
        // engTitle.setHorizontalAlignment(SwingConstants.CENTER);

        // ================= TITLE (THAI) =================
        GridBagConstraints titleThai = (GridBagConstraints) gbc.clone();
        titleThai.gridx = 0;
        titleThai.gridy = 0;
        titleThai.gridwidth = 2;
        // titleThai.insets = new Insets(25, 20, 5, 20);

        add(thaiTitle, titleThai);

        // // ================= TITLE (ENGLISH) =================
        // GridBagConstraints titleEng = (GridBagConstraints) gbc.clone();
        // titleEng.gridx = 0;
        // titleEng.gridy = 1;
        // titleEng.gridwidth = 2;
        // titleEng.insets = new Insets(0, 20, 30, 20);

        // add(engTitle, titleEng);

        // RESET for form section
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 20, 12, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;

        // ================= START LINE =================
        gbc.gridy++;
        gbc.gridx = 0;
        // add(label("Starting Line:"), gbc);
        add(label("สายต้นทาง:"), gbc);

        gbc.gridx = 1;
        lineComboStart = new JComboBox<>();
        for (Line line : controller.getLines()) {
            lineComboStart.addItem(line);
        }
        styleCombo(lineComboStart, "เลือกสายต้นทาง...");
        add(lineComboStart, gbc);

        // ================= START STATION =================
        gbc.gridy++;
        gbc.gridx = 0;
        // add(label("Starting Station:"), gbc);
        add(label("สถานีต้นทาง:"), gbc);

        gbc.gridx = 1;
        stationComboStart = new JComboBox<>();
        styleCombo(stationComboStart, "เลือกสถานีต้นทาง...");
        add(stationComboStart, gbc);

        // ================= END LINE =================
        gbc.gridy++;
        gbc.gridx = 0;
        // add(label("Destination Line:"), gbc);
        add(label("สายปลายทาง:"), gbc);

        gbc.gridx = 1;
        lineComboEnd = new JComboBox<>();
        for (Line line : controller.getLines()) {
            lineComboEnd.addItem(line);
        }
        styleCombo(lineComboEnd, "เลือกสายปลายทาง...");
        add(lineComboEnd, gbc);

        // ================= END STATION =================
        gbc.gridy++;
        gbc.gridx = 0;
        // add(label("Destination Station:"), gbc);
        add(label("สถานีปลายทาง:"), gbc);

        gbc.gridx = 1;
        stationComboEnd = new JComboBox<>();
        styleCombo(stationComboEnd, "เลือกสถานีปลายทาง...");
        add(stationComboEnd, gbc);

        // Add Listeners
        lineComboStart.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                updateStations(stationComboStart, (Line) e.getItem(), controller);
            }
        });

        lineComboEnd.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                updateStations(stationComboEnd, (Line) e.getItem(), controller);
            }
        });

        // Initialize stations empty
        lineComboStart.setSelectedIndex(-1);
        lineComboEnd.setSelectedIndex(-1);

        // ================= BUTTONS =================
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(245, 247, 250));

        // JButton clear = new JButton("Clear");
        JButton clear = new JButton("ล้างค่า");
        // JButton calc = new JButton("Find Route");
        JButton calc = new JButton("ค้นหาเส้นทาง");

        styleButton(clear, new Color(200, 200, 200), Color.BLACK);
        styleButton(calc, new Color(41, 128, 185), Color.WHITE);

        calc.addActionListener(e -> controller.onCalculateRoute());
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
        l.setFont(new Font("Tahoma", Font.PLAIN, 14));
        l.setForeground(new Color(60, 60, 60));
        return l;
    }

    private void styleCombo(JComboBox<?> box, String hint) {
        box.setFont(new Font("Tahoma", Font.PLAIN, 14));
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

    private void updateStations(JComboBox<Station> combo, Line line, RouteController controller) {
        combo.removeAllItems();
        if (line != null) {
            for (Station s : controller.getStationsByLine(line.code)) {
                combo.addItem(s);
            }
        }
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Tahoma", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(140, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
