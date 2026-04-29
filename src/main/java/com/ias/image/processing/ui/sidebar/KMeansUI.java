package com.ias.image.processing.ui.sidebar;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ias.image.processing.logic.operations.ImageOperation;
import com.ias.image.processing.logic.operations.KMeansOp;
import com.ias.image.processing.ui.MainFrame;

@SuppressWarnings("serial")
public class KMeansUI extends OperationUI {

    private JComboBox<String> colorSpaceBox;

    private JSlider kSlider, iterSlider, epsSlider;
    private JTextField kField, iterField, epsField;

    private boolean isUpdating = false;
    private MainFrame myMainFrame;

    public KMeansUI(MainFrame mainFrame, ImageOperation operation, int index) {
        super(mainFrame, operation, index);
        this.myMainFrame = mainFrame;
    }

    @Override
    protected JPanel createParametersPanel(ImageOperation operation) {
        KMeansOp kOp = (KMeansOp) operation;

        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));

        JPanel spacePanel = new JPanel(new GridLayout(1, 2, 2, 2));
        colorSpaceBox = new JComboBox<>(new String[]{"LAB", "BGR"});
        colorSpaceBox.setSelectedItem(kOp.getColorSpace());
        spacePanel.add(new JLabel("Color Space:"));
        spacePanel.add(colorSpaceBox);
        mainContainer.add(spacePanel);

        colorSpaceBox.addActionListener(e -> {
            updateOperationInformation();
            if (myMainFrame != null) myMainFrame.controller.processImage();
        });

        JPanel slidersContainer = new JPanel(new GridLayout(3, 1, 0, 5));

        kSlider = new JSlider(JSlider.HORIZONTAL, 2, 64, kOp.getKValue());
        kSlider.setMajorTickSpacing(15);
        kSlider.setPaintTicks(true);
        kField = new JTextField(Integer.toString(kOp.getKValue()), 4);
        slidersContainer.add(createSliderRow("Clusters (K):", kSlider, kField));

        iterSlider = new JSlider(JSlider.HORIZONTAL, 10, 200, kOp.getMaxIter());
        iterSlider.setMajorTickSpacing(50);
        iterSlider.setPaintTicks(true);
        iterField = new JTextField(Integer.toString(kOp.getMaxIter()), 4);
        slidersContainer.add(createSliderRow("Max Iterations:", iterSlider, iterField));

        int currentEpsInt = (int) Math.round(kOp.getEpsilon() * 100);
        epsSlider = new JSlider(JSlider.HORIZONTAL, 1, 200, currentEpsInt);
        epsField = new JTextField(Double.toString(kOp.getEpsilon()), 4);
        slidersContainer.add(createSliderRow("Epsilon:", epsSlider, epsField));

        mainContainer.add(slidersContainer);

        setupSliderLink(kSlider, kField, false);
        setupSliderLink(iterSlider, iterField, false);
        setupSliderLink(epsSlider, epsField, true);

        return mainContainer;
    }

    private JPanel createSliderRow(String label, JSlider slider, JTextField field) {
        JPanel row = new JPanel(new BorderLayout(5, 0));
        row.add(new JLabel(label), BorderLayout.WEST);
        row.add(slider, BorderLayout.CENTER);
        row.add(field, BorderLayout.EAST);
        return row;
    }

    private void setupSliderLink(JSlider slider, JTextField field, boolean isDouble) {
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!isUpdating) {
                    try {
                        isUpdating = true;

                        if (isDouble) {
                            double realVal = slider.getValue() / 100.0;
                            field.setText(String.valueOf(realVal));
                        } else {
                            field.setText(String.valueOf(slider.getValue()));
                        }

                        updateOperationInformation();

                        if (!slider.getValueIsAdjusting() && myMainFrame != null) {
                            myMainFrame.controller.processImage();
                        }
                    } finally {
                        isUpdating = false;
                    }
                }
            }
        });

        field.addActionListener(e -> {
            if (!isUpdating) {
                try {
                    isUpdating = true;
                    if (isDouble) {
                        double val = Double.parseDouble(field.getText().trim());
                        val = Math.max(0.01, Math.min(2.0, val));
                        field.setText(String.valueOf(val));
                        slider.setValue((int) Math.round(val * 100));
                    } else {
                        int val = Integer.parseInt(field.getText().trim());
                        val = Math.max(slider.getMinimum(), Math.min(slider.getMaximum(), val));
                        field.setText(String.valueOf(val));
                        slider.setValue(val);
                    }
                    updateOperationInformation();
                    if (myMainFrame != null) myMainFrame.controller.processImage();
                } catch (NumberFormatException ex) {
                    if (isDouble) field.setText(String.valueOf(slider.getValue() / 100.0));
                    else field.setText(String.valueOf(slider.getValue()));
                } finally {
                    isUpdating = false;
                }
            }
        });
    }

    @Override
    protected void updateOperationInformation() {
        KMeansOp kOp = (KMeansOp) this.operation;

        int kValue = kSlider.getValue();
        String colorSpace = (String) colorSpaceBox.getSelectedItem();
        int maxIter = iterSlider.getValue();
        double epsilon = epsSlider.getValue() / 100.0;

        kOp.updateOperation(kValue, colorSpace, maxIter, epsilon);
    }
}