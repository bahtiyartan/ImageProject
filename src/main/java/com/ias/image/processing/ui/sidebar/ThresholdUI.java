package com.ias.image.processing.ui.sidebar;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.opencv.imgproc.Imgproc;

import com.ias.image.processing.logic.operations.ImageOperation;
import com.ias.image.processing.logic.operations.ThresholdOp;
import com.ias.image.processing.ui.MainFrame;

@SuppressWarnings("serial")
public class ThresholdUI extends OperationUI {

    private JComboBox<String> modeBox;
    private JTextField maxValField;
    private JComboBox<String> typeBox;
    private JTextField threshField;
    private JComboBox<String> adaptiveMethodBox;
    private JTextField blockSizeField;
    private JTextField cField;

    private JPanel cards;
    private CardLayout cardLayout;

    public ThresholdUI(MainFrame mainFrame, ImageOperation operation, int index) {
        super(mainFrame, operation, index);
    }

    @Override
    protected JPanel createParametersPanel(ImageOperation operation) {
        ThresholdOp tOp = (ThresholdOp) operation;


        modeBox = new JComboBox<>(new String[]{"Simple", "Adaptive", "Otsu"});
        modeBox.setSelectedItem(tOp.getMode());

        maxValField = new JTextField(Double.toString(tOp.getMaxVal()), 5);
        maxValField.addFocusListener(this);

        typeBox = new JComboBox<>();
        if ("Adaptive".equals(tOp.getMode())) {
            typeBox.addItem("BINARY");
            typeBox.addItem("BINARY_INV");
        } else {
            typeBox.addItem("BINARY");
            typeBox.addItem("BINARY_INV");
            typeBox.addItem("TRUNC");
            typeBox.addItem("TOZERO");
            typeBox.addItem("TOZERO_INV");
        }

        int tType = tOp.getThresholdType();
        if (tType == Imgproc.THRESH_BINARY) typeBox.setSelectedItem("BINARY");
        else if (tType == Imgproc.THRESH_BINARY_INV) typeBox.setSelectedItem("BINARY_INV");
        else if (tType == Imgproc.THRESH_TRUNC) typeBox.setSelectedItem("TRUNC");
        else if (tType == Imgproc.THRESH_TOZERO) typeBox.setSelectedItem("TOZERO");
        else if (tType == Imgproc.THRESH_TOZERO_INV) typeBox.setSelectedItem("TOZERO_INV");
        typeBox.addActionListener(this);

        threshField = new JTextField(Double.toString(tOp.getThresh()), 5);
        threshField.addFocusListener(this);

        String[] adaptMethods = { "MEAN_C", "GAUSSIAN_C" };
        adaptiveMethodBox = new JComboBox<>(adaptMethods);
        if (tOp.getAdaptiveMethod() == Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C) adaptiveMethodBox.setSelectedItem("GAUSSIAN_C");
        else adaptiveMethodBox.setSelectedItem("MEAN_C");
        adaptiveMethodBox.addActionListener(this);

        blockSizeField = new JTextField(Integer.toString(tOp.getBlockSize()), 5);
        blockSizeField.addFocusListener(this);

        cField = new JTextField(Double.toString(tOp.getC()), 5);
        cField.addFocusListener(this);


        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));

        JPanel modePanel = new JPanel(new GridLayout(1, 2, 2, 2));
        modePanel.add(new JLabel("Threshold Mode:"));
        modePanel.add(modeBox);
        mainContainer.add(modePanel);

        JPanel commonPanel = new JPanel(new GridLayout(2, 2, 2, 2));
        commonPanel.add(new JLabel("Max Value:"));
        commonPanel.add(maxValField);
        commonPanel.add(new JLabel("Type:"));
        commonPanel.add(typeBox);
        mainContainer.add(commonPanel);

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        // Simple
        JPanel simpleCard = new JPanel(new GridLayout(1, 2, 2, 2));
        simpleCard.add(new JLabel("Threshold Val:"));
        simpleCard.add(threshField);
        cards.add(simpleCard, "Simple");

        // Adaptive
        JPanel adaptiveCard = new JPanel(new GridLayout(3, 2, 2, 2));
        adaptiveCard.add(new JLabel("Adaptive Method:"));
        adaptiveCard.add(adaptiveMethodBox);
        adaptiveCard.add(new JLabel("Block Size:"));
        adaptiveCard.add(blockSizeField);
        adaptiveCard.add(new JLabel("C Value:"));
        adaptiveCard.add(cField);
        cards.add(adaptiveCard, "Adaptive");

        // Otsu
        JPanel otsuCard = new JPanel(new BorderLayout());
        cards.add(otsuCard, "Otsu");

        mainContainer.add(cards);

        cardLayout.show(cards, tOp.getMode());

        modeBox.addActionListener(e -> {
            String selectedMode = (String) modeBox.getSelectedItem();
            cardLayout.show(cards, selectedMode);

            String currentType = (String) typeBox.getSelectedItem();
            typeBox.removeAllItems();

            if ("Adaptive".equals(selectedMode)) {
                typeBox.addItem("BINARY");
                typeBox.addItem("BINARY_INV");

                if ("BINARY".equals(currentType) || "BINARY_INV".equals(currentType)) {
                    typeBox.setSelectedItem(currentType);
                } else {
                    typeBox.setSelectedItem("BINARY");
                }
            } else {
                String[] allTypes = { "BINARY", "BINARY_INV", "TRUNC", "TOZERO", "TOZERO_INV" };
                for (String t : allTypes) {
                    typeBox.addItem(t);
                }
                typeBox.setSelectedItem(currentType);
            }


            mainContainer.revalidate();
            mainContainer.repaint();
            updateOperationInformation();
        });

        return mainContainer;
    }

    @Override
    protected void updateOperationInformation() {
        try {
            ThresholdOp tOp = (ThresholdOp) this.operation;

            String mode = (String) modeBox.getSelectedItem();
            double maxVal = Double.parseDouble(maxValField.getText().trim());

            String typeStr = (String) typeBox.getSelectedItem();
            int type = Imgproc.THRESH_BINARY;
            if ("BINARY_INV".equals(typeStr)) type = Imgproc.THRESH_BINARY_INV;
            else if ("TRUNC".equals(typeStr)) type = Imgproc.THRESH_TRUNC;
            else if ("TOZERO".equals(typeStr)) type = Imgproc.THRESH_TOZERO;
            else if ("TOZERO_INV".equals(typeStr)) type = Imgproc.THRESH_TOZERO_INV;

            double thresh = Double.parseDouble(threshField.getText().trim());

            String adaptStr = (String) adaptiveMethodBox.getSelectedItem();
            int adaptMethod = "GAUSSIAN_C".equals(adaptStr) ? Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C : Imgproc.ADAPTIVE_THRESH_MEAN_C;

            int blockSize = Integer.parseInt(blockSizeField.getText().trim());
            double C = Double.parseDouble(cField.getText().trim());

            tOp.updateOperation(mode, maxVal, type, thresh, adaptMethod, blockSize, C);

        } catch (NumberFormatException ex) {
        }
    }
}