package com.ias.image.processing.ui.sidebar;

import java.awt.GridLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.ias.image.processing.logic.operations.ImageOperation;
import com.ias.image.processing.logic.operations.GrayscaleOp;
import com.ias.image.processing.ui.MainFrame;

@SuppressWarnings("serial")
public class GrayscaleUI extends OperationUI {

    private JComboBox<String> methodBox;

    public GrayscaleUI(MainFrame mainFrame, ImageOperation operation, int index) {
        super(mainFrame, operation, index);
    }

    @Override
    protected JPanel createParametersPanel(ImageOperation operation) {

        GrayscaleOp grayOp = (GrayscaleOp) operation;

        String[] methods = { "Luminance", "Average", "Simple" };
        methodBox = new JComboBox<>(methods);

        String currentMethod = grayOp.getMethod();
        if (currentMethod != null && !currentMethod.isEmpty()) {
            methodBox.setSelectedItem(currentMethod);
        } else {
            methodBox.setSelectedItem("Luminance");
        }

        methodBox.addActionListener(this);

        JPanel panel = new JPanel(new GridLayout(1, 2, 2, 2));
        panel.add(new JLabel("Method:"));
        panel.add(methodBox);

        return panel;
    }

    @Override
    protected void updateOperationInformation() {
        String selectedMethod = (String) methodBox.getSelectedItem();
        GrayscaleOp gop = (GrayscaleOp) this.operation;
        gop.updateOperation(selectedMethod);
    }
}