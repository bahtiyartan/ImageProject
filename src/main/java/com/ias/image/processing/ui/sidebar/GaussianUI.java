package com.ias.image.processing.ui.sidebar;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.ias.image.processing.logic.operations.ImageOperation;
import com.ias.image.processing.logic.operations.GaussianBlurOp;
import com.ias.image.processing.ui.MainFrame;
import org.opencv.core.Core;

public class GaussianUI extends OperationUI {
    public GaussianUI(MainFrame mainFrame, ImageOperation operation, int index) {
        super(mainFrame, operation, index);
    }

    @Override
    protected JPanel createParametersPanel(ImageOperation operation) {
        JPanel panel = new JPanel(new GridLayout(3, 1, 2, 2));
        if (operation instanceof GaussianBlurOp blurOp) {
            String borderName = "Default";
            switch (blurOp.getBorderType()) {
                case Core.BORDER_CONSTANT:
                    borderName = "Constant";
                    break;
                case Core.BORDER_REPLICATE:
                    borderName = " Replicate";
                    break;
                case Core.BORDER_REFLECT:
                    borderName = "Reflect";
                    break;
            }

            panel.add(new JLabel("Kernel Size:" + blurOp.getKernelSize()));
            panel.add(new JLabel("Sigma X:" + blurOp.getSigmaX()));
            panel.add(new JLabel("Border" + borderName));
        } else {
            panel.add(new JLabel("Error: Not a Gaussian Blur Operation"));
        }
        return panel;
    }

}
