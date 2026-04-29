package com.ias.image.processing.ui.sidebar;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ias.image.processing.logic.operations.ImageOperation;
import com.ias.image.processing.logic.operations.RotateOp;
import com.ias.image.processing.ui.MainFrame;

@SuppressWarnings("serial")
public class RotateUI extends OperationUI {

    private JSlider angleSlider;
    private JTextField angleField;
    private JComboBox<String> hintBox;

    private boolean isUpdating = false;
    private MainFrame myMainFrame;

    public RotateUI(MainFrame mainFrame, ImageOperation operation, int index) {
        super(mainFrame, operation, index);
        this.myMainFrame = mainFrame;
    }

    @Override
    protected JPanel createParametersPanel(ImageOperation operation) {

        RotateOp rotateOp = (RotateOp) operation;
        int currentAngle = (int) Math.round(rotateOp.getAngle());

        angleSlider = new JSlider(JSlider.HORIZONTAL, 0, 360, currentAngle);
        angleSlider.setMajorTickSpacing(90);
        angleSlider.setMinorTickSpacing(15);
        angleSlider.setPaintTicks(true);

        angleField = new JTextField(String.valueOf(currentAngle), 4);

        angleSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!isUpdating) {
                    try {
                        isUpdating = true;
                        angleField.setText(String.valueOf(angleSlider.getValue()));
                        updateOperationInformation();

                        if (!angleSlider.getValueIsAdjusting()) {
                            myMainFrame.controller.processImage();
                        }
                    } finally {
                        isUpdating = false;
                    }
                }
            }
        });

        angleField.addActionListener(e -> {
            if (!isUpdating) {
                try {
                    isUpdating = true;
                    int val = Integer.parseInt(angleField.getText().trim());
                    val = Math.max(0, Math.min(360, val));
                    angleField.setText(String.valueOf(val));
                    angleSlider.setValue(val);
                    updateOperationInformation();
                    myMainFrame.controller.processImage();
                } catch (NumberFormatException ex) {
                    angleField.setText(String.valueOf(angleSlider.getValue()));
                } finally {
                    isUpdating = false;
                }
            }
        });

        String[] hints = { "Bicubic", "Bilinear", "Nearest Neighbor" };
        hintBox = new JComboBox<>(hints);

        String currentHint = rotateOp.getHintName();
        if (currentHint != null && !currentHint.isEmpty()) {
            hintBox.setSelectedItem(currentHint);
        } else {
            hintBox.setSelectedItem("Bicubic");
        }

        hintBox.addActionListener(e -> {
            updateOperationInformation();
            myMainFrame.controller.processImage();
        });

        JPanel panel = new JPanel(new BorderLayout(5, 5));

        JPanel anglePanel = new JPanel(new BorderLayout(5, 0));
        anglePanel.add(new JLabel("Angle:"), BorderLayout.WEST);
        anglePanel.add(angleSlider, BorderLayout.CENTER);
        anglePanel.add(angleField, BorderLayout.EAST);

        JPanel hintPanel = new JPanel(new BorderLayout(5, 0));
        hintPanel.add(new JLabel("Interpolation:"), BorderLayout.WEST);
        hintPanel.add(hintBox, BorderLayout.CENTER);

        JPanel container = new JPanel(new GridLayout(2, 1, 0, 5));
        container.add(anglePanel);
        container.add(hintPanel);

        panel.add(container, BorderLayout.NORTH);

        return panel;
    }

    @Override
    protected void updateOperationInformation() {
        double newAngle = angleSlider.getValue();
        String newHintName = (String) hintBox.getSelectedItem();

        Object hintObject = RenderingHints.VALUE_INTERPOLATION_BICUBIC;
        if ("Bilinear".equals(newHintName)) {
            hintObject = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
        } else if ("Nearest Neighbor".equals(newHintName)) {
            hintObject = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
        }

        RotateOp rop = (RotateOp) this.operation;
        rop.updateOperation(newAngle, hintObject, newHintName);
    }
}