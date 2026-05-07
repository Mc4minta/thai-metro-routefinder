package com.routefinder.view;

import com.routefinder.controller.RouteController;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ResultPanel extends JPanel {

    private final RouteController controller;
    private JPanel routeContainer;
    private JLabel totalFareLabel;
    private JLabel subtitleLabel;

    public ResultPanel(RouteController controller) {
        this.controller = controller;

        setLayout(new BorderLayout());
        setBackground(new Color(236, 240, 245));

        // ================= HEADER =================
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(new Color(236, 240, 245));

        JLabel titleLabel = new JLabel("ผลลัพธ์การค้นหาเส้นทาง");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 22));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        subtitleLabel = new JLabel("");
        subtitleLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerPanel.add(subtitleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        add(headerPanel, BorderLayout.NORTH);

        // ================= ROUTE =================
        routeContainer = new JPanel();
        routeContainer.setLayout(new BoxLayout(routeContainer, BoxLayout.Y_AXIS));
        routeContainer.setBackground(new Color(236, 240, 245));

        JScrollPane scrollPane = new JScrollPane(routeContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(new Color(236, 240, 245));

        add(scrollPane, BorderLayout.CENTER);

        // ================= BOTTOM =================
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(new Color(236, 240, 245));

        totalFareLabel = new JLabel("ราคารวม: 0 บาท", SwingConstants.CENTER);
        totalFareLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        totalFareLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton back = new JButton("ย้อนกลับ");
        styleButton(back);
        back.addActionListener(e -> controller.onBackToInput());

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(236, 240, 245));
        btnPanel.add(back);

        bottom.add(totalFareLabel, BorderLayout.NORTH);
        bottom.add(btnPanel, BorderLayout.SOUTH);

        add(bottom, BorderLayout.SOUTH);
    }

    // ================= RESULT =================
    public void setResult(List<Object[]> rows, double totalFare, String startStationName, String endStationName) {

        routeContainer.removeAll();

        if (startStationName != null && endStationName != null) {
            subtitleLabel.setText(startStationName + "  →  " + endStationName);
        } else {
            subtitleLabel.setText("");
        }

        for (int i = 0; i < rows.size(); i++) {

            Object[] row = rows.get(i);
            String rowType = String.valueOf(row[0]);

            if ("TRANSFER".equals(rowType)) {
                String fromLine = String.valueOf(row[1]);
                String toLine = String.valueOf(row[2]);
                String fromStation = String.valueOf(row[3]);
                String toStation = String.valueOf(row[4]);
                routeContainer.add(Box.createRigidArea(new Dimension(0, 5)));
                routeContainer.add(createTransferPanel(fromLine, toLine, fromStation, toStation));
                routeContainer.add(Box.createRigidArea(new Dimension(0, 5)));
                continue;
            }

            if ("STEP".equals(rowType)) {
                String startStation = String.valueOf(row[1]);
                String endStation = String.valueOf(row[2]);
                String lineCode = String.valueOf(row[3]);
                String fare = String.valueOf(row[4]);

                if (i > 0 && !"TRANSFER".equals(String.valueOf(rows.get(i - 1)[0]))) {
                    routeContainer.add(Box.createRigidArea(new Dimension(0, 10)));
                }

                routeContainer.add(createStepCard(startStation, endStation, lineCode, fare));
            }
        }

        routeContainer.add(Box.createRigidArea(new Dimension(0, 10)));

        totalFareLabel.setText("ราคารวม: " + formatBaht(totalFare));

        routeContainer.revalidate();
        routeContainer.repaint();
    }

    public void showNoRoute() {
        routeContainer.removeAll();
        subtitleLabel.setText("");

        JPanel errorPanel = new JPanel();
        errorPanel.setLayout(new BoxLayout(errorPanel, BoxLayout.Y_AXIS));
        errorPanel.setBackground(Color.WHITE);
        errorPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(231, 76, 60), 2, true),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)));

        JLabel iconLabel = new JLabel("⚠", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Tahoma", Font.BOLD, 48));
        iconLabel.setForeground(new Color(231, 76, 60));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel errorMsg = new JLabel("ไม่พบเส้นทาง", SwingConstants.CENTER);
        errorMsg.setFont(new Font("Tahoma", Font.BOLD, 20));
        errorMsg.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subErrorMsg = new JLabel("No route found between the selected stations.", SwingConstants.CENTER);
        subErrorMsg.setFont(new Font("Tahoma", Font.PLAIN, 14));
        subErrorMsg.setForeground(Color.GRAY);
        subErrorMsg.setAlignmentX(Component.CENTER_ALIGNMENT);

        errorPanel.add(iconLabel);
        errorPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        errorPanel.add(errorMsg);
        errorPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        errorPanel.add(subErrorMsg);

        errorPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorPanel.setMaximumSize(new Dimension(400, 200));

        routeContainer.add(Box.createVerticalGlue());
        routeContainer.add(errorPanel);
        routeContainer.add(Box.createVerticalGlue());

        totalFareLabel.setText("");

        routeContainer.revalidate();
        routeContainer.repaint();
    }

    // ================= FIXED TRANSFER =================
    private JPanel createTransferPanel(String fromLine, String toLine, String fromStation, String toStation) {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        panel.setOpaque(false);

        Color cFrom = getLineColor(fromLine);
        Color cTo = getLineColor(toLine);

        JLabel prefix = new JLabel("เปลี่ยนสายจาก ");
        JLabel from = new JLabel(fromLine);
        JLabel middle = new JLabel(" ไป ");
        JLabel to = new JLabel(toLine);
        JLabel suffix = new JLabel(" ที่สถานี ");
        JLabel fromStationLbl = new JLabel(fromStation);
        JLabel transferArrow = new JLabel(" ไป ");
        JLabel toStationLbl = new JLabel(toStation);

        Font font = new Font("Tahoma", Font.BOLD, 14);

        prefix.setFont(font);
        prefix.setForeground(Color.BLACK);
        middle.setFont(font);
        middle.setForeground(Color.BLACK);
        suffix.setFont(font);
        suffix.setForeground(Color.BLACK);

        from.setFont(font);
        from.setForeground(cFrom);

        to.setFont(font);
        to.setForeground(cTo);

        fromStationLbl.setFont(font);
        fromStationLbl.setForeground(cFrom);

        transferArrow.setFont(font);
        transferArrow.setForeground(Color.DARK_GRAY);

        toStationLbl.setFont(font);
        toStationLbl.setForeground(cTo);

        panel.add(prefix);
        panel.add(from);
        panel.add(middle);
        panel.add(to);
        panel.add(suffix);
        panel.add(fromStationLbl);
        panel.add(transferArrow);
        panel.add(toStationLbl);

        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        return panel;
    }

    // ================= STEP CARD =================
    private JPanel createStepCard(String startStation, String endStation, String lineCode, String fare) {

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);

        Color lineColor = getLineColor(lineCode);

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230), 1, true),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 5, 0, 0, lineColor),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15))));

        JLabel lineBadge = new JLabel(" " + lineCode + " ");
        lineBadge.setOpaque(true);
        lineBadge.setBackground(lineColor);
        lineBadge.setForeground(getContrastColor(lineColor));
        lineBadge.setFont(new Font("Tahoma", Font.BOLD, 12));

        JLabel stationsLabel = new JLabel(startStation + "  →  " + endStation);
        stationsLabel.setFont(new Font("Tahoma", Font.BOLD, 14));

        JLabel fareLabel = new JLabel(formatBaht(Double.parseDouble(fare)));
        fareLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 0, 15);
        card.add(lineBadge, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        card.add(stationsLabel, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        card.add(fareLabel, gbc);

        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        return card;
    }

    // ================= HELPERS =================
    private Color getContrastColor(Color c) {
        double luminance = (0.299 * c.getRed() + 0.587 * c.getGreen() + 0.114 * c.getBlue()) / 255;
        return luminance > 0.5 ? Color.BLACK : Color.WHITE;
    }

    private Color getLineColor(String code) {
        return controller.getLineColor(code);
    }


    private void styleButton(JButton btn) {
        btn.setBackground(new Color(231, 76, 60));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Tahoma", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private String formatBaht(double amount) {
        if (Math.abs(amount - Math.rint(amount)) < 0.0001) {
            return String.format("%.0f บาท", amount);
        }
        return String.format("%.2f บาท", amount);
    }

}
