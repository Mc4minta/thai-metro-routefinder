package com.routefinder.view;

import com.routefinder.controller.RouteController;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel cardPanel;

    private static final String INPUT = "INPUT";
    private static final String RESULT = "RESULT";

    public MainFrame(RouteController controller) {
        // setTitle("Thai Metro Route Finder");
        setTitle("โปรแกรมค้นหาเส้นทางรถไฟฟ้าไทย");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setSize(920, 620);
        setLocationRelativeTo(null);

        // modern background
        getContentPane().setBackground(new Color(236, 240, 245));

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(new Color(236, 240, 245));

        InputPanel inputPanel = new InputPanel(controller);
        ResultPanel resultPanel = new ResultPanel(controller);

        cardPanel.add(inputPanel, INPUT);
        cardPanel.add(resultPanel, RESULT);

        add(cardPanel);

        showInput();
    }

    public void showInput() {
        cardLayout.show(cardPanel, INPUT);
    }

    public void showResult() {
        cardLayout.show(cardPanel, RESULT);
    }
}
