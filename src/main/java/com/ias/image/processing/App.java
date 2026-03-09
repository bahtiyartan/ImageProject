package com.ias.image.processing;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.*;
import java.io.File;
import com.ias.image.processing.logic.ImageController;
import com.ias.image.processing.logic.ImageModel;
import com.ias.image.processing.ui.MainFrame;

public class App {
    public static void main(String[] args) {
        nu.pattern.OpenCV.loadShared();
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
            }
            ImageModel model = new ImageModel();
            ImageController controller = new ImageController(model);
            MainFrame mainFrame = new MainFrame(controller);

            if (args != null && args.length > 0) {
                File projectFile = new File(args[0]);
                if (projectFile.exists()) {
                    try {
                        controller.loadProject(projectFile);
                        mainFrame.refreshUI();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(mainFrame,
                                "An error occurred while uploading the project.: " + e.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }

            mainFrame.setVisible(true);
        });
    }
}