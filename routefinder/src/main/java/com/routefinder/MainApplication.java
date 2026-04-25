package com.routefinder;

import com.routefinder.service.StationDataService;
import com.routefinder.ui.InputPanel;
import com.routefinder.ui.ResultPanel;

import javax.swing.*;
import java.awt.*;

public class MainApplication extends JFrame {

    private CardLayout cardLayout;
    private JPanel cardPanel;
    private StationDataService dataService;

    private static final String INPUT = "INPUT";
    private static final String RESULT = "RESULT";

    public MainApplication() {

        setTitle("Route Finder");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setSize(920, 620);
        setLocationRelativeTo(null);

        // modern background
        getContentPane().setBackground(new Color(236, 240, 245));

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(new Color(236, 240, 245));

        dataService = new StationDataService();

        InputPanel inputPanel = new InputPanel(this);
        ResultPanel resultPanel = new ResultPanel(this);

        cardPanel.add(inputPanel, INPUT);
        cardPanel.add(resultPanel, RESULT);

        add(cardPanel);

        showInput();
    }

    public StationDataService getDataService() {
        return dataService;
    }

    public void showInput() {
        cardLayout.show(cardPanel, INPUT);
    }

    public void showResult() {
        cardLayout.show(cardPanel, RESULT);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainApplication().setVisible(true));
    }
}