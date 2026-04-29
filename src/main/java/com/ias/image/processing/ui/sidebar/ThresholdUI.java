package com.ias.image.processing.ui.sidebar;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.opencv.imgproc.Imgproc;

import com.ias.image.processing.logic.operations.ImageOperation;
import com.ias.image.processing.logic.operations.ThresholdOp;
import com.ias.image.processing.ui.MainFrame;

@SuppressWarnings("serial")
public class ThresholdUI extends OperationUI {

    private JComboBox<String> modeBox;
    private JComboBox<String> typeBox;
    private JComboBox<String> adaptiveMethodBox;

    private JSlider maxValSlider, threshSlider, blockSizeSlider, cSlider;
    private JTextField maxValField, threshField, blockSizeField, cField;

    private JPanel cards;
    private CardLayout cardLayout;

    private boolean isUpdating = false;
    private MainFrame myMainFrame;

    public ThresholdUI(MainFrame mainFrame, ImageOperation operation, int index) {
        super(mainFrame, operation, index);
        this.myMainFrame = mainFrame;
    }

    @Override
    protected JPanel createParametersPanel(ImageOperation operation) {
        ThresholdOp tOp = (ThresholdOp) operation;

        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));

        modeBox = new JComboBox<>(new String[]{"Simple", "Adaptive", "Otsu"});
        modeBox.setSelectedItem(tOp.getMode());

        JPanel modePanel = new JPanel(new GridLayout(1, 2, 2, 2));
        modePanel.add(new JLabel("Threshold Mode:"));
        modePanel.add(modeBox);
        mainContainer.add(modePanel);

        JPanel commonPanel = new JPanel(new GridLayout(2, 1, 0, 5));

        // Max Value (0-255)
        maxValSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, (int)tOp.getMaxVal());
        maxValSlider.setMajorTickSpacing(85);
        maxValSlider.setPaintTicks(true);
        maxValField = new JTextField(String.valueOf((int)tOp.getMaxVal()), 4);
        commonPanel.add(createRow("Max Value:", maxValSlider, maxValField));

        typeBox = new JComboBox<>();
        updateTypeBoxItems(tOp.getMode());
        setSelectedType(tOp.getThresholdType());

        JPanel typeRow = new JPanel(new BorderLayout(5, 0));
        typeRow.add(new JLabel("Type:"), BorderLayout.WEST);
        typeRow.add(typeBox, BorderLayout.CENTER);
        commonPanel.add(typeRow);

        mainContainer.add(commonPanel);

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        JPanel simpleCard = new JPanel(new GridLayout(1, 1, 0, 5));
        threshSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, (int)tOp.getThresh());
        threshField = new JTextField(String.valueOf((int)tOp.getThresh()), 4);
        simpleCard.add(createRow("Threshold Val:", threshSlider, threshField));
        cards.add(simpleCard, "Simple");

        JPanel adaptiveCard = new JPanel(new GridLayout(3, 1, 0, 5));
        adaptiveMethodBox = new JComboBox<>(new String[]{"MEAN_C", "GAUSSIAN_C"});
        adaptiveMethodBox.setSelectedItem(tOp.getAdaptiveMethod() == Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C ? "GAUSSIAN_C" : "MEAN_C");
        JPanel adaptRow = new JPanel(new BorderLayout(5, 0));
        adaptRow.add(new JLabel("Adaptive Method:"), BorderLayout.WEST);
        adaptRow.add(adaptiveMethodBox, BorderLayout.CENTER);
        adaptiveCard.add(adaptRow);

        blockSizeSlider = new JSlider(JSlider.HORIZONTAL, 3, 99, tOp.getBlockSize());
        blockSizeField = new JTextField(String.valueOf(tOp.getBlockSize()), 4);
        adaptiveCard.add(createRow("Block Size (Odd):", blockSizeSlider, blockSizeField));

        int currentC = (int) Math.round(tOp.getC() * 10);
        cSlider = new JSlider(JSlider.HORIZONTAL, -500, 500, currentC);
        cField = new JTextField(String.valueOf(tOp.getC()), 4);
        adaptiveCard.add(createRow("C Value:", cSlider, cField));

        cards.add(adaptiveCard, "Adaptive");

        JPanel otsuCard = new JPanel(new BorderLayout());
        cards.add(otsuCard, "Otsu");

        mainContainer.add(cards);
        cardLayout.show(cards, tOp.getMode());

        modeBox.addActionListener(e -> {
            String selectedMode = (String) modeBox.getSelectedItem();
            cardLayout.show(cards, selectedMode);
            updateTypeBoxItems(selectedMode);
            mainContainer.revalidate();
            mainContainer.repaint();
            updateOperationInformation();
            if (myMainFrame != null) myMainFrame.controller.processImage();
        });

        typeBox.addActionListener(e -> triggerUpdate());
        adaptiveMethodBox.addActionListener(e -> triggerUpdate());

        setupSlider(maxValSlider, maxValField, false, false);
        setupSlider(threshSlider, threshField, false, false);
        setupSlider(blockSizeSlider, blockSizeField, true, false);
        setupSlider(cSlider, cField, false, true);

        return mainContainer;
    }

    private JPanel createRow(String label, JSlider slider, JTextField field) {
        JPanel row = new JPanel(new BorderLayout(5, 0));
        row.add(new JLabel(label), BorderLayout.WEST);
        row.add(slider, BorderLayout.CENTER);
        row.add(field, BorderLayout.EAST);
        return row;
    }

    private void setupSlider(JSlider slider, JTextField field, boolean forceOdd, boolean isDouble) {
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!isUpdating) {
                    try {
                        isUpdating = true;
                        int val = slider.getValue();

                        if (forceOdd && val % 2 == 0) val += 1;

                        if (isDouble) {
                            field.setText(String.valueOf(val / 10.0));
                        } else {
                            field.setText(String.valueOf(val));
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
                        val = Math.max(-50.0, Math.min(50.0, val));
                        field.setText(String.valueOf(val));
                        slider.setValue((int) Math.round(val * 10));
                    } else {
                        int val = Integer.parseInt(field.getText().trim());
                        val = Math.max(slider.getMinimum(), Math.min(slider.getMaximum(), val));
                        if (forceOdd && val % 2 == 0) val += 1;
                        field.setText(String.valueOf(val));
                        slider.setValue(val);
                    }
                    updateOperationInformation();
                    if (myMainFrame != null) myMainFrame.controller.processImage();
                } catch (NumberFormatException ex) {
                } finally {
                    isUpdating = false;
                }
            }
        });
    }

    private void updateTypeBoxItems(String mode) {
        String currentType = (String) typeBox.getSelectedItem();
        typeBox.removeAllItems();

        if ("Adaptive".equals(mode)) {
            typeBox.addItem("BINARY");
            typeBox.addItem("BINARY_INV");
            if ("BINARY".equals(currentType) || "BINARY_INV".equals(currentType)) {
                typeBox.setSelectedItem(currentType);
            } else {
                typeBox.setSelectedItem("BINARY");
            }
        } else {
            String[] allTypes = { "BINARY", "BINARY_INV", "TRUNC", "TOZERO", "TOZERO_INV" };
            for (String t : allTypes) typeBox.addItem(t);
            typeBox.setSelectedItem(currentType != null ? currentType : "BINARY");
        }
    }

    private void setSelectedType(int tType) {
        if (tType == Imgproc.THRESH_BINARY) typeBox.setSelectedItem("BINARY");
        else if (tType == Imgproc.THRESH_BINARY_INV) typeBox.setSelectedItem("BINARY_INV");
        else if (tType == Imgproc.THRESH_TRUNC) typeBox.setSelectedItem("TRUNC");
        else if (tType == Imgproc.THRESH_TOZERO) typeBox.setSelectedItem("TOZERO");
        else if (tType == Imgproc.THRESH_TOZERO_INV) typeBox.setSelectedItem("TOZERO_INV");
    }

    private void triggerUpdate() {
        if (!isUpdating) {
            updateOperationInformation();
            if (myMainFrame != null) myMainFrame.controller.processImage();
        }
    }

    @Override
    protected void updateOperationInformation() {
        ThresholdOp tOp = (ThresholdOp) this.operation;

        String mode = (String) modeBox.getSelectedItem();
        double maxVal = maxValSlider.getValue();

        String typeStr = (String) typeBox.getSelectedItem();
        int type = Imgproc.THRESH_BINARY;
        if ("BINARY_INV".equals(typeStr)) type = Imgproc.THRESH_BINARY_INV;
        else if ("TRUNC".equals(typeStr)) type = Imgproc.THRESH_TRUNC;
        else if ("TOZERO".equals(typeStr)) type = Imgproc.THRESH_TOZERO;
        else if ("TOZERO_INV".equals(typeStr)) type = Imgproc.THRESH_TOZERO_INV;

        double thresh = threshSlider.getValue();

        String adaptStr = (String) adaptiveMethodBox.getSelectedItem();
        int adaptMethod = "GAUSSIAN_C".equals(adaptStr) ? Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C : Imgproc.ADAPTIVE_THRESH_MEAN_C;

        int blockSize = blockSizeSlider.getValue();
        if (blockSize % 2 == 0) blockSize += 1;

        double cValue = cSlider.getValue() / 10.0;

        tOp.updateOperation(mode, maxVal, type, thresh, adaptMethod, blockSize, cValue);
    }
}