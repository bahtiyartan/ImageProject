package com.ias.image.processing.ui.sidebar;

import java.awt.GridLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.ias.image.processing.logic.operations.ImageOperation;
import com.ias.image.processing.logic.operations.KMeansOp;
import com.ias.image.processing.ui.MainFrame;

@SuppressWarnings("serial")
public class KMeansUI extends OperationUI {

    private JTextField kField;
    private JComboBox<String> colorSpaceBox;
    private JTextField maxIterField;
    private JTextField epsilonField;

    public KMeansUI(MainFrame mainFrame, ImageOperation operation, int index) {
        super(mainFrame, operation, index);
    }

    @Override
    protected JPanel createParametersPanel(ImageOperation operation) {
        KMeansOp kOp = (KMeansOp) operation;

        kField = new JTextField(Integer.toString(kOp.getKValue()), 5);
        kField.addFocusListener(this);

        colorSpaceBox = new JComboBox<>(new String[]{"LAB", "BGR"});
        colorSpaceBox.setSelectedItem(kOp.getColorSpace());
        colorSpaceBox.addActionListener(this);

        maxIterField = new JTextField(Integer.toString(kOp.getMaxIter()), 5);
        maxIterField.addFocusListener(this);

        epsilonField = new JTextField(Double.toString(kOp.getEpsilon()), 5);
        epsilonField.addFocusListener(this);

        JPanel panel = new JPanel(new GridLayout(4, 2, 2, 2));
        panel.add(new JLabel("Clusters (K):"));
        panel.add(kField);
        panel.add(new JLabel("Color Space:"));
        panel.add(colorSpaceBox);
        panel.add(new JLabel("Max Iterations:"));
        panel.add(maxIterField);
        panel.add(new JLabel("Epsilon:"));
        panel.add(epsilonField);

        return panel;
    }

    @Override
    protected void updateOperationInformation() {
        try {
            KMeansOp kOp = (KMeansOp) this.operation;

            int kValue = Integer.parseInt(kField.getText().trim());
            String colorSpace = (String) colorSpaceBox.getSelectedItem();
            int maxIter = Integer.parseInt(maxIterField.getText().trim());
            double epsilon = Double.parseDouble(epsilonField.getText().trim());

            kOp.updateOperation(kValue, colorSpace, maxIter, epsilon);

        } catch (NumberFormatException ex) {
        }
    }
}