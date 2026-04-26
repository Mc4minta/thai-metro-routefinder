package com.routefinder.view;

import com.routefinder.controller.RouteController;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Test extends JPanel {

    private JPanel routeContainer;
    private JLabel totalFareLabel;
    private JLabel subtitleLabel;

    public Test(RouteController controller) {

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

        totalFareLabel = new JLabel("ราคารวม: ฿0.00", SwingConstants.CENTER);
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

        // ================= MOCK DATA =================
        boolean USE_MOCK = true;

        if (USE_MOCK) {
            java.util.List<Object[]> mockRows = new java.util.ArrayList<>();

            mockRows.add(new Object[] {
                    "ท่าพระ (Tha Phra)",
                    "เตาปูน (Tao Poon)",
                    "BL",
                    "16.00"
            });

            mockRows.add(new Object[] {
                    "เตาปูน (Tao Poon)",
                    "ศูนย์ราชการนนทบุรี (Nonthaburi Civic Center)",
                    "PP",
                    "15.00"
            });

            mockRows.add(new Object[] {
                    "ศูนย์ราชการนนทบุรี",
                    "วัดพระศรีมหาธาตุ (Wat Phra Sri Mahathat)",
                    "PK",
                    "18.00"
            });

            setResult(mockRows, 49.00);
        }
    }

    // ================= RESULT =================
    public void setResult(List<Object[]> rows, double totalFare) {

        routeContainer.removeAll();

        if (rows != null && !rows.isEmpty()) {
            String overallStart = String.valueOf(rows.get(0)[0]);
            String overallEnd = String.valueOf(rows.get(rows.size() - 1)[1]);
            subtitleLabel.setText(overallStart + "  →  " + overallEnd);
        } else {
            subtitleLabel.setText("");
        }

        for (int i = 0; i < rows.size(); i++) {

            Object[] row = rows.get(i);
            String startStation = String.valueOf(row[0]);
            String endStation = String.valueOf(row[1]);
            String lineCode = String.valueOf(row[2]);
            String fare = String.valueOf(row[3]);

            // ===== TRANSFER =====
            if (i > 0) {
                String prevLine = String.valueOf(rows.get(i - 1)[2]);

                if (!lineCode.equals(prevLine)) {
                    routeContainer.add(Box.createRigidArea(new Dimension(0, 5)));
                    routeContainer.add(createTransferPanel(prevLine, lineCode, startStation));
                    routeContainer.add(Box.createRigidArea(new Dimension(0, 5)));
                } else {
                    routeContainer.add(Box.createRigidArea(new Dimension(0, 10)));
                }
            }

            routeContainer.add(createStepCard(startStation, endStation, lineCode, fare));
        }

        routeContainer.add(Box.createRigidArea(new Dimension(0, 10)));

        totalFareLabel.setText(String.format("ราคารวม: ฿%.2f", totalFare));

        routeContainer.revalidate();
        routeContainer.repaint();
    }

    // ================= FIXED TRANSFER =================
    private JPanel createTransferPanel(String fromLine, String toLine, String station) {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        panel.setOpaque(false);

        Color cFrom = getLineColor(fromLine);
        Color cTo = getLineColor(toLine);

        JLabel prefix = new JLabel("เปลี่ยนสายจาก ");
        JLabel from = new JLabel(fromLine);
        JLabel middle = new JLabel(" ไป ");
        JLabel to = new JLabel(toLine);
        JLabel suffix = new JLabel(" ที่สถานี ");
        JLabel stationLbl = new JLabel(station);

        Font font = new Font("Tahoma", Font.BOLD, 14);

        prefix.setFont(font);
        middle.setFont(font);
        suffix.setFont(font);

        from.setFont(font);
        from.setForeground(cFrom);

        to.setFont(font);
        to.setForeground(cTo);

        // ✅ ใช้สีสายต้นทาง
        stationLbl.setFont(font);
        stationLbl.setForeground(cFrom);

        panel.add(prefix);
        panel.add(from);
        panel.add(middle);
        panel.add(to);
        panel.add(suffix);
        panel.add(stationLbl);

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

        JLabel fareLabel = new JLabel("฿" + fare);
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
        switch (code) {
            case "PP":
                return new Color(102, 0, 102);
            case "BL":
                return new Color(21, 102, 181);
            case "PK":
                return new Color(207, 88, 130);
            case "YL":
                return new Color(241, 217, 13);
            case "ARL":
                return new Color(100, 38, 40);
            case "RN":
                return new Color(221, 7, 11);
            case "LG":
                return new Color(113, 185, 38);
            case "DG":
                return new Color(3, 129, 125);
            default:
                return Color.GRAY;
        }
    }

    private void styleButton(JButton btn) {
        btn.setBackground(new Color(231, 76, 60));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Tahoma", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}