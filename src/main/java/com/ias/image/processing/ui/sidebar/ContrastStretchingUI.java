package com.ias.image.processing.ui.sidebar;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.ias.image.processing.logic.operations.ImageOperation;
import com.ias.image.processing.logic.operations.ContrastStretchingOp;
import com.ias.image.processing.ui.MainFrame;

@SuppressWarnings("serial")
public class ContrastStretchingUI extends OperationUI {

    private JComboBox<String> modeBox;
    private JTextField tMinField;
    private JTextField tMaxField;
    private JTextField sMinField;
    private JTextField sMaxField;

    private JPanel cards;
    private CardLayout cardLayout;

    public ContrastStretchingUI(MainFrame mainFrame, ImageOperation operation, int index) {
        super(mainFrame, operation, index);
    }

    @Override
    protected JPanel createParametersPanel(ImageOperation operation) {
        ContrastStretchingOp csOp = (ContrastStretchingOp) operation;

        modeBox = new JComboBox<>(new String[]{"Auto (Min-Max)", "Custom (Robust)"});
        modeBox.setSelectedItem(csOp.getMode());

        tMinField = new JTextField(Double.toString(csOp.getTargetMin()), 5);
        tMaxField = new JTextField(Double.toString(csOp.getTargetMax()), 5);
        sMinField = new JTextField(Double.toString(csOp.getSourceMin()), 5);
        sMaxField = new JTextField(Double.toString(csOp.getSourceMax()), 5);

        tMinField.addFocusListener(this);
        tMaxField.addFocusListener(this);
        sMinField.addFocusListener(this);
        sMaxField.addFocusListener(this);

        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));

        JPanel modePanel = new JPanel(new GridLayout(1, 2, 2, 2));
        modePanel.add(new JLabel("Mode:"));
        modePanel.add(modeBox);
        mainContainer.add(modePanel);

        JPanel targetPanel = new JPanel(new GridLayout(2, 2, 2, 2));
        targetPanel.add(new JLabel("Target Min (a):"));
        targetPanel.add(tMinField);
        targetPanel.add(new JLabel("Target Max (b):"));
        targetPanel.add(tMaxField);
        mainContainer.add(targetPanel);

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        JPanel autoCard = new JPanel(new BorderLayout());
        cards.add(autoCard, "Auto (Min-Max)");

        JPanel customCard = new JPanel(new GridLayout(2, 2, 2, 2));
        customCard.add(new JLabel("Source Min (c):"));
        customCard.add(sMinField);
        customCard.add(new JLabel("Source Max (d):"));
        customCard.add(sMaxField);
        cards.add(customCard, "Custom (Robust)");

        mainContainer.add(cards);
        cardLayout.show(cards, csOp.getMode());

        modeBox.addActionListener(e -> {
            String selectedMode = (String) modeBox.getSelectedItem();
            cardLayout.show(cards, selectedMode);
            mainContainer.revalidate();
            mainContainer.repaint();
            updateOperationInformation();
        });

        return mainContainer;
    }

    @Override
    protected void updateOperationInformation() {
        try {
            ContrastStretchingOp csOp = (ContrastStretchingOp) this.operation;

            String mode = (String) modeBox.getSelectedItem();
            double tMin = Double.parseDouble(tMinField.getText().trim());
            double tMax = Double.parseDouble(tMaxField.getText().trim());
            double sMin = Double.parseDouble(sMinField.getText().trim());
            double sMax = Double.parseDouble(sMaxField.getText().trim());

            csOp.updateOperation(mode, tMin, tMax, sMin, sMax);

        } catch (NumberFormatException ex) {
        }
    }
}