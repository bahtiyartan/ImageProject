package com.ias.image.processing;

import com.ias.image.processing.logic.ImageController;
import com.ias.image.processing.logic.ImageModel;
import com.ias.image.processing.ui.MainFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class App {
    public static void main(String[] args) {
        nu.pattern.OpenCV.loadShared();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            ImageModel model = new ImageModel();

            ImageController controller = new ImageController(model);

            MainFrame mainFrame = new MainFrame(controller);

            mainFrame.setVisible(true);
        });
    }
}