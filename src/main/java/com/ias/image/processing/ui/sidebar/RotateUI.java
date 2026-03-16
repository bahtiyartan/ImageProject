package com.ias.image.processing.ui.sidebar;

import java.awt.GridLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.ias.image.processing.logic.operations.ImageOperation;
import com.ias.image.processing.logic.operations.RotateOp;
import com.ias.image.processing.ui.MainFrame;

public class RotateUI extends OperationUI {

    public RotateUI(MainFrame mainFrame, ImageOperation operation, int index) {
        super(mainFrame, operation, index);
    }

    @Override
    protected JPanel createParametersPanel(ImageOperation operation) {

        RotateOp rotateOp = (RotateOp) operation;

        JTextField angleField = new JTextField(Double.toString(rotateOp.getAngle()), 5);

        String[] hints = { "Bicubic", "Bilinear", "Nearest Neighbor" };
        JComboBox<String> hintBox = new JComboBox<>(hints);

        String currentHint = rotateOp.getHintName();

        if (currentHint != null && !currentHint.isEmpty()) {
            hintBox.setSelectedItem(currentHint);
        } else {
            hintBox.setSelectedItem("Bicubic");
        }

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Angle (degrees):"));
        panel.add(angleField);
        panel.add(new JLabel("Interpolation:"));
        panel.add(hintBox);

        return panel;
    }
}