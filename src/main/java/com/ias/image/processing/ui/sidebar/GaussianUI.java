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
import org.opencv.core.Core;

import com.ias.image.processing.logic.operations.GaussianBlurOp;
import com.ias.image.processing.logic.operations.ImageOperation;
import com.ias.image.processing.ui.MainFrame;

@SuppressWarnings("serial")
public class GaussianUI extends OperationUI {

    private JSlider kernelSlider;
    private JTextField kernelField;

    private JSlider sigmaSlider;
    private JTextField sigmaField;

    private JComboBox<String> borderBox;

    private boolean isUpdating = false;
    private MainFrame myMainFrame;

    public GaussianUI(MainFrame mainFrame, ImageOperation operation, int index) {
        super(mainFrame, operation, index);
        this.myMainFrame = mainFrame;
    }

    @Override
    protected JPanel createParametersPanel(ImageOperation operation) {

        GaussianBlurOp blurOp = (GaussianBlurOp) operation;

        int currentKernel = blurOp.getKernelSize();
        kernelSlider = new JSlider(JSlider.HORIZONTAL, 1, 99, currentKernel);
        kernelSlider.setMajorTickSpacing(20);
        kernelSlider.setPaintTicks(true);
        kernelField = new JTextField(Integer.toString(currentKernel), 4);

        kernelSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!isUpdating) {
                    try {
                        isUpdating = true;
                        int val = kernelSlider.getValue();
                        if (val % 2 == 0) val += 1;

                        kernelField.setText(String.valueOf(val));
                        updateOperationInformation();

                        if (!kernelSlider.getValueIsAdjusting()) {
                            myMainFrame.controller.processImage();
                        }
                    } finally {
                        isUpdating = false;
                    }
                }
            }
        });

        kernelField.addActionListener(e -> {
            if (!isUpdating) {
                try {
                    isUpdating = true;
                    int val = Integer.parseInt(kernelField.getText().trim());
                    val = Math.max(1, Math.min(99, val));
                    if (val % 2 == 0) val += 1;
                    kernelField.setText(String.valueOf(val));
                    kernelSlider.setValue(val);
                    updateOperationInformation();
                    myMainFrame.controller.processImage();
                } catch (NumberFormatException ex) {
                    kernelField.setText(String.valueOf(kernelSlider.getValue()));
                } finally {
                    isUpdating = false;
                }
            }
        });

        int currentSigmaInt = (int) Math.round(blurOp.getSigmaX() * 10);
        sigmaSlider = new JSlider(JSlider.HORIZONTAL, 0, 200, currentSigmaInt);
        sigmaField = new JTextField(Double.toString(blurOp.getSigmaX()), 4);

        sigmaSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!isUpdating) {
                    try {
                        isUpdating = true;
                        double realSigma = sigmaSlider.getValue() / 10.0;
                        sigmaField.setText(String.valueOf(realSigma));
                        updateOperationInformation();

                        if (!sigmaSlider.getValueIsAdjusting()) {
                            myMainFrame.controller.processImage();
                        }
                    } finally {
                        isUpdating = false;
                    }
                }
            }
        });

        sigmaField.addActionListener(e -> {
            if (!isUpdating) {
                try {
                    isUpdating = true;
                    double val = Double.parseDouble(sigmaField.getText().trim());
                    val = Math.max(0.0, Math.min(20.0, val));
                    sigmaField.setText(String.valueOf(val));
                    sigmaSlider.setValue((int) Math.round(val * 10));
                    updateOperationInformation();
                    myMainFrame.controller.processImage();
                } catch (NumberFormatException ex) {
                    sigmaField.setText(String.valueOf(sigmaSlider.getValue() / 10.0));
                } finally {
                    isUpdating = false;
                }
            }
        });

        String[] borderNames = { "DEFAULT", "CONSTANT", "REPLICATE", "REFLECT" };
        borderBox = new JComboBox<>(borderNames);

        int currentBorder = blurOp.getBorderType();
        if (currentBorder == Core.BORDER_CONSTANT) borderBox.setSelectedItem("CONSTANT");
        else if (currentBorder == Core.BORDER_REPLICATE) borderBox.setSelectedItem("REPLICATE");
        else if (currentBorder == Core.BORDER_REFLECT) borderBox.setSelectedItem("REFLECT");
        else borderBox.setSelectedItem("DEFAULT");

        borderBox.addActionListener(e -> {
            updateOperationInformation();
            myMainFrame.controller.processImage();
        });

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JPanel mainContainer = new JPanel(new GridLayout(3, 1, 0, 8));

        JPanel kernelPanel = new JPanel(new BorderLayout(5, 0));
        kernelPanel.add(new JLabel("Kernel Size:"), BorderLayout.WEST);
        kernelPanel.add(kernelSlider, BorderLayout.CENTER);
        kernelPanel.add(kernelField, BorderLayout.EAST);

        JPanel sigmaPanel = new JPanel(new BorderLayout(5, 0));
        sigmaPanel.add(new JLabel("Sigma X:"), BorderLayout.WEST);
        sigmaPanel.add(sigmaSlider, BorderLayout.CENTER);
        sigmaPanel.add(sigmaField, BorderLayout.EAST);

        JPanel borderPanel = new JPanel(new BorderLayout(5, 0));
        borderPanel.add(new JLabel("Border Type:"), BorderLayout.WEST);
        borderPanel.add(borderBox, BorderLayout.CENTER);

        mainContainer.add(kernelPanel);
        mainContainer.add(sigmaPanel);
        mainContainer.add(borderPanel);

        panel.add(mainContainer, BorderLayout.NORTH);

        return panel;
    }

    @Override
    protected void updateOperationInformation() {
        int newKernel = kernelSlider.getValue();
        if (newKernel % 2 == 0) newKernel += 1;

        double newSigma = sigmaSlider.getValue() / 10.0;

        String selectedBorder = (String) borderBox.getSelectedItem();
        int newBorderType = Core.BORDER_DEFAULT;
        if ("CONSTANT".equals(selectedBorder)) newBorderType = Core.BORDER_CONSTANT;
        else if ("REPLICATE".equals(selectedBorder)) newBorderType = Core.BORDER_REPLICATE;
        else if ("REFLECT".equals(selectedBorder)) newBorderType = Core.BORDER_REFLECT;

        GaussianBlurOp gop = (GaussianBlurOp) this.operation;
        gop.updateOperation(newKernel, newSigma, newBorderType);
    }
}