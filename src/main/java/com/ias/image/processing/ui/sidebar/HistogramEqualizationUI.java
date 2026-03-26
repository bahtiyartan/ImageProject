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
import com.ias.image.processing.logic.operations.HistogramEqualizationOp;
import com.ias.image.processing.ui.MainFrame;

@SuppressWarnings("serial")
public class HistogramEqualizationUI extends OperationUI {

    private JComboBox<String> modeBox;
    private JTextField clipLimitField;
    private JTextField tileSizeField;

    private JPanel cards;
    private CardLayout cardLayout;

    public HistogramEqualizationUI(MainFrame mainFrame, ImageOperation operation, int index) {
        super(mainFrame, operation, index);
    }

    @Override
    protected JPanel createParametersPanel(ImageOperation operation) {
        HistogramEqualizationOp heOp = (HistogramEqualizationOp) operation;

        modeBox = new JComboBox<>(new String[]{"Standard", "CLAHE"});
        modeBox.setSelectedItem(heOp.getMode());

        clipLimitField = new JTextField(Double.toString(heOp.getClipLimit()), 5);
        clipLimitField.addFocusListener(this);

        tileSizeField = new JTextField(Integer.toString(heOp.getTileSize()), 5);
        tileSizeField.addFocusListener(this);

        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));

        JPanel modePanel = new JPanel(new GridLayout(1, 2, 2, 2));
        modePanel.add(new JLabel("Mode:"));
        modePanel.add(modeBox);
        mainContainer.add(modePanel);

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        JPanel standardCard = new JPanel(new BorderLayout());
        cards.add(standardCard, "Standard");

        JPanel claheCard = new JPanel(new GridLayout(2, 2, 2, 2));
        claheCard.add(new JLabel("Clip Limit:"));
        claheCard.add(clipLimitField);
        claheCard.add(new JLabel("Tile Grid Size:"));
        claheCard.add(tileSizeField);
        cards.add(claheCard, "CLAHE");

        mainContainer.add(cards);
        cardLayout.show(cards, heOp.getMode());

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
            HistogramEqualizationOp heOp = (HistogramEqualizationOp) this.operation;

            String mode = (String) modeBox.getSelectedItem();
            double clipLimit = Double.parseDouble(clipLimitField.getText().trim());
            int tileSize = Integer.parseInt(tileSizeField.getText().trim());

            heOp.updateOperation(mode, clipLimit, tileSize);

        } catch (NumberFormatException ex) {
        }
    }
}