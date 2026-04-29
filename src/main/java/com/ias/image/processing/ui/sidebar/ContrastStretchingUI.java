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

import com.ias.image.processing.logic.operations.ImageOperation;
import com.ias.image.processing.logic.operations.ContrastStretchingOp;
import com.ias.image.processing.ui.MainFrame;

@SuppressWarnings("serial")
public class ContrastStretchingUI extends OperationUI {

    private JComboBox<String> modeBox;

    private JSlider tMinSlider, tMaxSlider, sMinSlider, sMaxSlider;
    private JTextField tMinField, tMaxField, sMinField, sMaxField;

    private JPanel cards;
    private CardLayout cardLayout;

    private boolean isUpdating = false;
    private MainFrame myMainFrame;

    public ContrastStretchingUI(MainFrame mainFrame, ImageOperation operation, int index) {
        super(mainFrame, operation, index);
        this.myMainFrame = mainFrame;
    }

    @Override
    protected JPanel createParametersPanel(ImageOperation operation) {
        ContrastStretchingOp csOp = (ContrastStretchingOp) operation;

        modeBox = new JComboBox<>(new String[]{"Auto (Min-Max)", "Custom (Robust)"});
        modeBox.setSelectedItem(csOp.getMode());

        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));

        JPanel modePanel = new JPanel(new GridLayout(1, 2, 2, 2));
        modePanel.add(new JLabel("Mode:"));
        modePanel.add(modeBox);
        mainContainer.add(modePanel);

        JPanel targetPanel = new JPanel(new GridLayout(2, 1, 0, 5));

        tMinSlider = createSlider((int)csOp.getTargetMin());
        tMinField = createTextField((int)csOp.getTargetMin());
        targetPanel.add(createSliderRow("Target Min (a):", tMinSlider, tMinField));

        tMaxSlider = createSlider((int)csOp.getTargetMax());
        tMaxField = createTextField((int)csOp.getTargetMax());
        targetPanel.add(createSliderRow("Target Max (b):", tMaxSlider, tMaxField));

        mainContainer.add(targetPanel);

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        JPanel autoCard = new JPanel(new BorderLayout());
        cards.add(autoCard, "Auto (Min-Max)");

        JPanel customCard = new JPanel(new GridLayout(2, 1, 0, 5));
        sMinSlider = createSlider((int)csOp.getSourceMin());
        sMinField = createTextField((int)csOp.getSourceMin());
        customCard.add(createSliderRow("Source Min (c):", sMinSlider, sMinField));

        sMaxSlider = createSlider((int)csOp.getSourceMax());
        sMaxField = createTextField((int)csOp.getSourceMax());
        customCard.add(createSliderRow("Source Max (d):", sMaxSlider, sMaxField));

        cards.add(customCard, "Custom (Robust)");

        mainContainer.add(cards);
        cardLayout.show(cards, csOp.getMode());

        modeBox.addActionListener(e -> {
            String selectedMode = (String) modeBox.getSelectedItem();
            cardLayout.show(cards, selectedMode);
            mainContainer.revalidate();
            mainContainer.repaint();
            updateOperationInformation();
            myMainFrame.controller.processImage();
        });

        setupSliderLink(tMinSlider, tMinField);
        setupSliderLink(tMaxSlider, tMaxField);
        setupSliderLink(sMinSlider, sMinField);
        setupSliderLink(sMaxSlider, sMaxField);

        return mainContainer;
    }

    private JSlider createSlider(int initialValue) {
        JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 255, initialValue);
        slider.setMajorTickSpacing(85);
        slider.setPaintTicks(true);
        return slider;
    }

    private JTextField createTextField(int initialValue) {
        return new JTextField(String.valueOf(initialValue), 4);
    }

    private JPanel createSliderRow(String label, JSlider slider, JTextField field) {
        JPanel row = new JPanel(new BorderLayout(5, 0));
        row.add(new JLabel(label), BorderLayout.WEST);
        row.add(slider, BorderLayout.CENTER);
        row.add(field, BorderLayout.EAST);
        return row;
    }

    private void setupSliderLink(JSlider slider, JTextField field) {
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!isUpdating) {
                    try {
                        isUpdating = true;
                        field.setText(String.valueOf(slider.getValue()));
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
                    val = Math.max(0, Math.min(255, val)); // 0-255 Sınırı
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

    @Override
    protected void updateOperationInformation() {
        ContrastStretchingOp csOp = (ContrastStretchingOp) this.operation;

        String mode = (String) modeBox.getSelectedItem();
        double tMin = tMinSlider.getValue();
        double tMax = tMaxSlider.getValue();
        double sMin = sMinSlider.getValue();
        double sMax = sMaxSlider.getValue();

        csOp.updateOperation(mode, tMin, tMax, sMin, sMax);
    }
}