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
    private JButton calc;

    public InputPanel(RouteController controller) {

        setBackground(new Color(245, 247, 250));
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 20, 12, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel thaiTitle = new JLabel("โปรแกรมค้นหาเส้นทางรถไฟฟ้าไทย");
        thaiTitle.setFont(new Font("Tahoma", Font.BOLD, 26));
        thaiTitle.setHorizontalAlignment(SwingConstants.CENTER);

        // ================= TITLE (THAI) =================
        GridBagConstraints titleThai = (GridBagConstraints) gbc.clone();
        titleThai.gridx = 0;
        titleThai.gridy = 0;
        titleThai.gridwidth = 2;

        add(thaiTitle, titleThai);

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
        add(label("สายต้นทาง:"), gbc);

        gbc.gridx = 1;
        lineComboStart = new JComboBox<>();
        for (Line line : controller.getLines()) {
            lineComboStart.addItem(line);
        }
        styleCombo(lineComboStart, "เลือกสายต้นทาง...", Color.WHITE);
        add(lineComboStart, gbc);

        // ================= START STATION =================
        gbc.gridy++;
        gbc.gridx = 0;
        add(label("สถานีต้นทาง:"), gbc);

        gbc.gridx = 1;
        stationComboStart = new JComboBox<>();
        styleCombo(stationComboStart, "เลือกสถานีต้นทาง...", Color.WHITE);
        add(stationComboStart, gbc);

        // ================= END LINE =================
        gbc.gridy++;
        gbc.gridx = 0;
        add(label("สายปลายทาง:"), gbc);

        gbc.gridx = 1;
        lineComboEnd = new JComboBox<>();
        for (Line line : controller.getLines()) {
            lineComboEnd.addItem(line);
        }
        styleCombo(lineComboEnd, "เลือกสายปลายทาง...", Color.WHITE);
        add(lineComboEnd, gbc);

        // ================= END STATION =================
        gbc.gridy++;
        gbc.gridx = 0;
        add(label("สถานีปลายทาง:"), gbc);

        gbc.gridx = 1;
        stationComboEnd = new JComboBox<>();
        styleCombo(stationComboEnd, "เลือกสถานีปลายทาง...", Color.WHITE);
        add(stationComboEnd, gbc);

        // Add Listeners
        lineComboStart.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Line selected = (Line) e.getItem();
                updateStations(stationComboStart, selected, controller);
                stationComboStart.setEnabled(true);

                // Dynamic coloring happens here when a line is selected
                Color bg = selected.color != null ? selected.color : Color.WHITE;
                Color fg = getContrastColor(bg);
                lineComboStart.setBackground(bg);
                lineComboStart.setForeground(fg);
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                stationComboStart.setEnabled(false);
                stationComboStart.removeAllItems();

                // Reset both to Color.WHITE when deselected
                lineComboStart.setBackground(Color.WHITE);
                lineComboStart.setForeground(Color.BLACK);
                stationComboStart.setBackground(Color.WHITE);
                stationComboStart.setForeground(Color.BLACK);
            }
            validateInputs();
        });

        lineComboEnd.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Line selected = (Line) e.getItem();
                updateStations(stationComboEnd, selected, controller);
                stationComboEnd.setEnabled(true);

                // Dynamic coloring happens here when a line is selected
                Color bg = selected.color != null ? selected.color : Color.WHITE;
                Color fg = getContrastColor(bg);
                lineComboEnd.setBackground(bg);
                lineComboEnd.setForeground(fg);
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                stationComboEnd.setEnabled(false);
                stationComboEnd.removeAllItems();

                // Reset both to Color.WHITE when deselected
                lineComboEnd.setBackground(Color.WHITE);
                lineComboEnd.setForeground(Color.BLACK);
                stationComboEnd.setBackground(Color.WHITE);
                stationComboEnd.setForeground(Color.BLACK);
            }
            validateInputs();
        });

        stationComboStart.addActionListener(e -> {
            Station selected = (Station) stationComboStart.getSelectedItem();
            Line line = (Line) lineComboStart.getSelectedItem();
            if (selected != null && line != null && line.color != null) {
                Color bg = line.color;
                Color fg = getContrastColor(bg);
                stationComboStart.setBackground(bg);
                stationComboStart.setForeground(fg);
            } else {
                stationComboStart.setBackground(Color.WHITE);
                stationComboStart.setForeground(Color.BLACK);
            }
            validateInputs();
        });

        stationComboEnd.addActionListener(e -> {
            Station selected = (Station) stationComboEnd.getSelectedItem();
            Line line = (Line) lineComboEnd.getSelectedItem();
            if (selected != null && line != null && line.color != null) {
                Color bg = line.color;
                Color fg = getContrastColor(bg);
                stationComboEnd.setBackground(bg);
                stationComboEnd.setForeground(fg);
            } else {
                stationComboEnd.setBackground(Color.WHITE);
                stationComboEnd.setForeground(Color.BLACK);
            }
            validateInputs();
        });

        // ================= BUTTONS =================
        calc = new JButton("ค้นหาเส้นทาง");
        calc.setEnabled(false);
        styleButton(calc, new Color(41, 128, 185), Color.WHITE);

        // Initialize stations empty
        lineComboStart.setSelectedIndex(-1);
        lineComboEnd.setSelectedIndex(-1);
        stationComboStart.setEnabled(false);
        stationComboEnd.setEnabled(false);

        // ================= BUTTONS =================
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(245, 247, 250));

        JButton clear = new JButton("ล้างค่า");

        styleButton(clear, new Color(200, 200, 200), Color.BLACK);

        calc.addActionListener(e -> controller.onCalculateRoute());
        clear.addActionListener(e -> {
            lineComboStart.setSelectedIndex(-1);
            lineComboEnd.setSelectedIndex(-1);
            stationComboStart.removeAllItems();
            stationComboEnd.removeAllItems();
            stationComboStart.setEnabled(false);
            stationComboEnd.setEnabled(false);
            validateInputs();
        });

        buttonPanel.add(clear);
        buttonPanel.add(calc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        setFocusable(true);
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    private void validateInputs() {
        if (calc == null)
            return;
        boolean startSelected = stationComboStart.getSelectedItem() != null;
        boolean endSelected = stationComboEnd.getSelectedItem() != null;
        calc.setEnabled(startSelected && endSelected);
    }

    /**
     * Determines whether black or white text should be used based on the background
     * color's luminance for better readability.
     */
    private Color getContrastColor(Color c) {
        if (c == null || c.equals(Color.WHITE))
            return Color.BLACK;
        // Standard formula for relative luminance
        double luminance = (0.299 * c.getRed() + 0.587 * c.getGreen() + 0.114 * c.getBlue()) / 255;
        return luminance > 0.5 ? Color.BLACK : Color.WHITE;
    }

    // ================= UI HELPERS =================

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Tahoma", Font.PLAIN, 14));
        l.setForeground(new Color(60, 60, 60));
        return l;
    }

    private void styleCombo(JComboBox<?> box, String hint, Color bgColor) {
        box.setFont(new Font("Tahoma", Font.PLAIN, 14));
        box.setBackground(bgColor);
        // setOpaque(true) is needed because some L&Fs don't paint background for
        // JComboBox by default
        box.setOpaque(true);
        box.setPreferredSize(new Dimension(550, 35));
        box.setFocusable(false);

        box.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value == null && index == -1) {
                    setText(hint);
                    setForeground(Color.GRAY);
                    setBackground(Color.WHITE);
                } else {
                    Color bg = Color.WHITE;
                    Color fg = Color.BLACK;

                    if (value instanceof Line) {
                        Line line = (Line) value;
                        bg = line.color != null ? line.color : Color.WHITE;
                        fg = getContrastColor(bg);
                    } else if (value instanceof Station) {
                        // Use the current background of the combo box for stations
                        bg = box.getBackground();
                        fg = box.getForeground();
                    }

                    if (isSelected) {
                        setBackground(bg.darker());
                    } else {
                        setBackground(bg);
                    }
                    setForeground(fg);
                }

                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
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
        combo.setSelectedIndex(-1);
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
