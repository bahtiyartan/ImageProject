package com.ias.image.processing.ui.sidebar;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Font;
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
import com.ias.image.processing.logic.operations.EdgeDetectionOp;
import com.ias.image.processing.ui.MainFrame;

@SuppressWarnings("serial")
public class EdgeDetectionUI extends OperationUI {

    private JComboBox<String> algoBox;

    private JSlider t1Slider, t2Slider;
    private JTextField t1Field, t2Field;

    private JSlider dxSlider, dySlider, sobelKSlider;
    private JTextField dxField, dyField, sobelKField;

    private JSlider lapKSlider;
    private JTextField lapKField;

    private JPanel cards;
    private CardLayout cardLayout;

    private boolean isUpdating = false;
    private MainFrame myMainFrame;

    public EdgeDetectionUI(MainFrame mainFrame, ImageOperation operation, int index) {
        super(mainFrame, operation, index);
        this.myMainFrame = mainFrame;
    }

    @Override
    protected JPanel createParametersPanel(ImageOperation operation) {
        EdgeDetectionOp edOp = (EdgeDetectionOp) operation;

        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));

        JPanel algoPanel = new JPanel(new BorderLayout(2, 0));
        algoBox = new JComboBox<>(new String[]{"Canny", "Sobel", "Laplacian"});
        algoBox.setSelectedItem(edOp.getAlgorithm());
        JLabel l1 = new JLabel("Algorithm:"); l1.setFont(new Font(l1.getFont().getName(), Font.PLAIN, 11));
        algoPanel.add(l1, BorderLayout.WEST);
        algoPanel.add(algoBox, BorderLayout.CENTER);
        mainContainer.add(algoPanel);

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        JPanel cannyCard = new JPanel(new GridLayout(2, 1, 0, 2));
        t1Slider = new JSlider(JSlider.HORIZONTAL, 0, 255, (int)edOp.getCannyThresh1());
        t1Slider.setPaintTicks(false);
        t1Field = new JTextField(String.valueOf((int)edOp.getCannyThresh1()), 3);
        cannyCard.add(createSliderRow("Thresh 1:", t1Slider, t1Field));

        t2Slider = new JSlider(JSlider.HORIZONTAL, 0, 255, (int)edOp.getCannyThresh2());
        t2Slider.setPaintTicks(false);
        t2Field = new JTextField(String.valueOf((int)edOp.getCannyThresh2()), 3);
        cannyCard.add(createSliderRow("Thresh 2:", t2Slider, t2Field));
        cards.add(cannyCard, "Canny");


        JPanel sobelCard = new JPanel(new GridLayout(3, 1, 0, 2));
        dxSlider = new JSlider(JSlider.HORIZONTAL, 0, 2, edOp.getSobelDx());
        dxSlider.setPaintTicks(false);
        dxField = new JTextField(String.valueOf(edOp.getSobelDx()), 3);
        sobelCard.add(createSliderRow("Dx:", dxSlider, dxField));

        dySlider = new JSlider(JSlider.HORIZONTAL, 0, 2, edOp.getSobelDy());
        dySlider.setPaintTicks(false);
        dyField = new JTextField(String.valueOf(edOp.getSobelDy()), 3);
        sobelCard.add(createSliderRow("Dy:", dySlider, dyField));

        sobelKSlider = new JSlider(JSlider.HORIZONTAL, 1, 7, edOp.getKernelSize());
        sobelKSlider.setPaintTicks(false);
        sobelKField = new JTextField(String.valueOf(edOp.getKernelSize()), 3);
        sobelCard.add(createSliderRow("K.Size:", sobelKSlider, sobelKField));
        cards.add(sobelCard, "Sobel");



        JPanel lapCard = new JPanel(new GridLayout(1, 1, 0, 2));
        lapKSlider = new JSlider(JSlider.HORIZONTAL, 1, 7, edOp.getKernelSize());
        lapKSlider.setPaintTicks(false);
        lapKField = new JTextField(String.valueOf(edOp.getKernelSize()), 3);
        lapCard.add(createSliderRow("K.Size:", lapKSlider, lapKField));
        cards.add(lapCard, "Laplacian");


        mainContainer.add(cards);
        cardLayout.show(cards, edOp.getAlgorithm());

        algoBox.addActionListener(e -> {
            String selectedAlgo = (String) algoBox.getSelectedItem();
            cardLayout.show(cards, selectedAlgo);
            mainContainer.revalidate();
            mainContainer.repaint();
            triggerUpdate();
        });

        setupSlider(t1Slider, t1Field, false);
        setupSlider(t2Slider, t2Field, false);

        setupSlider(dxSlider, dxField, false);
        setupSlider(dySlider, dyField, false);
        setupSlider(sobelKSlider, sobelKField, true);

        setupSlider(lapKSlider, lapKField, true);

        return mainContainer;
    }

    private JPanel createSliderRow(String label, JSlider slider, JTextField field) {
        JPanel row = new JPanel(new BorderLayout(2, 0));
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font(lbl.getFont().getName(), Font.PLAIN, 11));
        row.add(lbl, BorderLayout.WEST);
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
        EdgeDetectionOp edOp = (EdgeDetectionOp) this.operation;

        String algo = (String) algoBox.getSelectedItem();
        double cT1 = t1Slider.getValue();
        double cT2 = t2Slider.getValue();
        int dx = dxSlider.getValue();
        int dy = dySlider.getValue();

        int kSize = 3;
        if ("Sobel".equals(algo)) {
            kSize = sobelKSlider.getValue();
        } else if ("Laplacian".equals(algo)) {
            kSize = lapKSlider.getValue();
        }

        edOp.updateOperation(algo, cT1, cT2, dx, dy, kSize);
    }
}