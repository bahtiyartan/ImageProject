package com.ias.image.processing.ui.sidebar;

import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.ias.image.processing.logic.operations.ImageOperation;
import com.ias.image.processing.logic.operations.ColorHistogramOp;
import com.ias.image.processing.ui.MainFrame;

public class ColorHistogramUI extends OperationUI {

    public ColorHistogramUI(MainFrame mainFrame, ImageOperation operation, int index) {
        super(mainFrame, operation, index);
    }
    @Override
    protected JPanel createParametersPanel(ImageOperation operation) {
        JPanel panel = new JPanel(new GridLayout(4, 1, 2, 2));

        if (operation instanceof ColorHistogramOp histOp) {

            panel.add(new JLabel("  Number of Tones (Bin): " + histOp.getBinCount()));

            JLabel rLabel = new JLabel(String.format("  RED: %.1f%%", histOp.getDomRed()));
            rLabel.setForeground(Color.RED);

            JLabel gLabel = new JLabel(String.format("  GREEN: %.1f%%", histOp.getDomGreen()));
            gLabel.setForeground(new Color(0, 150, 0));

            JLabel bLabel = new JLabel(String.format("  BLUE: %.1f%%", histOp.getDomBlue()));
            bLabel.setForeground(Color.BLUE);

            panel.add(rLabel);
            panel.add(gLabel);
            panel.add(bLabel);

        } else {
            panel.add(new JLabel("Error: Not a Histogram Operation"));
        }

        return panel;
    }
}