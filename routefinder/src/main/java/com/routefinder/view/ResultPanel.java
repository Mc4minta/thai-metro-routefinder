package com.routefinder.view;

import com.routefinder.controller.RouteController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ResultPanel extends JPanel {

    private JTable table;
    private JLabel totalFareLabel;

    public ResultPanel(RouteController controller) {

        setLayout(new BorderLayout());
        setBackground(new Color(236, 240, 245));

        // ================= HEADER =================
        JLabel title = new JLabel("Route Result", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        add(title, BorderLayout.NORTH);

        // ================= TABLE =================
        String[] columns = {"Start Station", "End Station", "Line", "Fare"};

        DefaultTableModel model = new DefaultTableModel(columns, 0);
        table = new JTable(model);

        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        add(scrollPane, BorderLayout.CENTER);

        // ================= BOTTOM PANEL =================
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(new Color(236, 240, 245));

        totalFareLabel = new JLabel("Total Fare: ฿0.00", SwingConstants.CENTER);
        totalFareLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalFareLabel.setForeground(new Color(39, 174, 96));
        totalFareLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton back = new JButton("Back");
        styleButton(back);
        back.addActionListener(e -> controller.onBackToInput());

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(236, 240, 245));
        btnPanel.add(back);

        bottom.add(totalFareLabel, BorderLayout.NORTH);
        bottom.add(btnPanel, BorderLayout.SOUTH);

        add(bottom, BorderLayout.SOUTH);
    }

    // ================= UPDATE RESULT =================
    public void setResult(List<Object[]> rows, double totalFare) {

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // clear old data

        for (Object[] row : rows) {
            model.addRow(row);
        }

        totalFareLabel.setText(String.format("Total Fare: ฿%.2f", totalFare));
    }

    private void styleButton(JButton btn) {
        btn.setBackground(new Color(231, 76, 60));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
