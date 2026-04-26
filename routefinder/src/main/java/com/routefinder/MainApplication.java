// src/main/java/com/routefinder/MainApplication.java

package com.routefinder;

import com.routefinder.controller.RouteController;
import com.routefinder.service.StationDataService;
import com.routefinder.view.MainFrame;

import javax.swing.SwingUtilities;

public class MainApplication {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // MVC Initialization
            StationDataService dataService = new StationDataService();
            RouteController controller = new RouteController(dataService);
            MainFrame mainFrame = new MainFrame(controller);

            // Link controller to view
            controller.setMainFrame(mainFrame);

            mainFrame.setVisible(true);
        });
    }
}