package com.ias.image.processing.ui.sidebar;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ias.image.processing.logic.operations.ImageOperation;
import com.ias.image.processing.logic.operations.MorphologyOp;
import com.ias.image.processing.ui.MainFrame;

@SuppressWarnings("serial")
public class MorphologyUI extends OperationUI {

    private JComboBox<String> typeBox;
    private JComboBox<String> shapeBox;

    private JSlider kernelSlider;
    private JTextField kernelSizeField;

    private JSlider iterSlider;
    private JTextField iterationsField;

    private boolean isUpdating = false;
    private MainFrame myMainFrame;

    public MorphologyUI(MainFrame mainFrame, ImageOperation operation, int index) {
        super(mainFrame, operation, index);
        this.myMainFrame = mainFrame;
    }

    @Override
    protected JPanel createParametersPanel(ImageOperation operation) {
        MorphologyOp mOp = (MorphologyOp) operation;

        JPanel mainContainer = new JPanel(new GridLayout(4, 1, 0, 8));

        JPanel typePanel = new JPanel(new BorderLayout(5, 0));
        typeBox = new JComboBox<>(new String[]{"Erosion", "Dilation", "Opening", "Closing"});
        typeBox.setSelectedItem(mOp.getMorphType());
        typePanel.add(new JLabel("Operation Type:"), BorderLayout.WEST);
        typePanel.add(typeBox, BorderLayout.CENTER);
        mainContainer.add(typePanel);

        JPanel shapePanel = new JPanel(new BorderLayout(5, 0));
        shapeBox = new JComboBox<>(new String[]{"Rect", "Cross", "Ellipse"});
        shapeBox.setSelectedItem(mOp.getKernelShape());
        shapePanel.add(new JLabel("Kernel Shape:"), BorderLayout.WEST);
        shapePanel.add(shapeBox, BorderLayout.CENTER);
        mainContainer.add(shapePanel);

        typeBox.addActionListener(e -> triggerUpdate());
        shapeBox.addActionListener(e -> triggerUpdate());

        kernelSlider = new JSlider(JSlider.HORIZONTAL, 1, 99, mOp.getKernelSize());
        kernelSlider.setMajorTickSpacing(20);
        kernelSlider.setPaintTicks(true);
        kernelSizeField = new JTextField(String.valueOf(mOp.getKernelSize()), 4);
        mainContainer.add(createSliderRow("Kernel Size (Odd):", kernelSlider, kernelSizeField));

        iterSlider = new JSlider(JSlider.HORIZONTAL, 1, 20, mOp.getIterations());
        iterSlider.setMajorTickSpacing(5);
        iterSlider.setPaintTicks(true);
        iterationsField = new JTextField(String.valueOf(mOp.getIterations()), 4);
        mainContainer.add(createSliderRow("Iterations:", iterSlider, iterationsField));

        setupSlider(kernelSlider, kernelSizeField, true);
        setupSlider(iterSlider, iterationsField, false);

        return mainContainer;
    }

    private JPanel createSliderRow(String label, JSlider slider, JTextField field) {
        JPanel row = new JPanel(new BorderLayout(5, 0));
        row.add(new JLabel(label), BorderLayout.WEST);
        row.add(slider, BorderLayout.CENTER);
        row.add(field, BorderLayout.EAST);
        return row;
    }

    private void setupSlider(JSlider slider, JTextField field, boolean forceOdd) {
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!isUpdating) {
                    try {
                        isUpdating = true;
                        int val = slider.getValue();

                        if (forceOdd && val % 2 == 0) val += 1;

                        field.setText(String.valueOf(val));
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
                    int val = Integer.parseInt(field.getText().trim());
                    val = Math.max(slider.getMinimum(), Math.min(slider.getMaximum(), val));

                    if (forceOdd && val % 2 == 0) val += 1;

                    field.setText(String.valueOf(val));
                    slider.setValue(val);
                    updateOperationInformation();

                    if (myMainFrame != null) myMainFrame.controller.processImage();
                } catch (NumberFormatException ex) {
                    field.setText(String.valueOf(slider.getValue()));
                } finally {
                    isUpdating = false;
                }
            }
        });
    }

    private void triggerUpdate() {
        if (!isUpdating) {
            updateOperationInformation();
            if (myMainFrame != null) myMainFrame.controller.processImage();
        }
    }

    @Override
    protected void updateOperationInformation() {
        MorphologyOp mOp = (MorphologyOp) this.operation;

        String type = (String) typeBox.getSelectedItem();
        String shape = (String) shapeBox.getSelectedItem();

        int size = kernelSlider.getValue();
        if (size % 2 == 0) size += 1;

        int iter = iterSlider.getValue();

        mOp.updateOperation(type, shape, size, iter);
    }
}