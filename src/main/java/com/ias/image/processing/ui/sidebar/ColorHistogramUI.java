package com.ias.image.processing.ui.sidebar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.ias.image.processing.logic.operations.ImageOperation;
import com.ias.image.processing.logic.operations.ColorHistogramOp;
import com.ias.image.processing.ui.MainFrame;

@SuppressWarnings("serial")
public class ColorHistogramUI extends OperationUI {

    private JTextField binCountField;

    private JLabel redLabel, greenLabel, blueLabel;
    private JTextArea resultArea;
    private JScrollPane scrollPane;
    private JButton toggleButton;

    public ColorHistogramUI(MainFrame mainFrame, ImageOperation operation, int index) {
        super(mainFrame, operation, index);
    }

    @Override
    protected JPanel createParametersPanel(ImageOperation operation) {
        ColorHistogramOp cOp = (ColorHistogramOp) operation;

        binCountField = new JTextField(Integer.toString(cOp.getBinCount()), 5);
        binCountField.addFocusListener(this);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JPanel paramPanel = new JPanel(new GridLayout(1, 2, 2, 2));
        paramPanel.add(new JLabel("Number of Tones (Bin):"));
        paramPanel.add(binCountField);
        mainPanel.add(paramPanel);

        JPanel colorPanel = new JPanel(new GridLayout(3, 1, 2, 2));
        colorPanel.setBorder(new TitledBorder("Dominant Colors"));

        redLabel = new JLabel("RED: 0%");
        redLabel.setForeground(Color.RED);
        greenLabel = new JLabel("GREEN: 0%");
        greenLabel.setForeground(new Color(0, 150, 0));
        blueLabel = new JLabel("BLUE: 0%");
        blueLabel.setForeground(Color.BLUE);

        colorPanel.add(redLabel);
        colorPanel.add(greenLabel);
        colorPanel.add(blueLabel);
        mainPanel.add(colorPanel);


        toggleButton = new JButton("Show Details ▼");
        toggleButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        toggleButton.setFocusable(false);
        mainPanel.add(toggleButton);

        //detaylı sonuclar
        resultArea = new JTextArea(8, 20);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        resultArea.setBackground(new Color(245, 245, 245));

        scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(new TitledBorder("Histogram Details"));
        scrollPane.setPreferredSize(new Dimension(250, 150));
        scrollPane.setVisible(false);
        mainPanel.add(scrollPane);

        toggleButton.addActionListener(e -> {
            boolean isVisible = scrollPane.isVisible();
            scrollPane.setVisible(!isVisible);

            if (!isVisible) {
                toggleButton.setText("Hide Details ▲");
            } else {
                toggleButton.setText("Show Details ▼");
            }

            mainPanel.revalidate();
            mainPanel.repaint();
        });

        cOp.setOnResultUpdated(() -> {
            redLabel.setText(String.format("RED: %.1f%%", cOp.getDomRed()));
            greenLabel.setText(String.format("GREEN: %.1f%%", cOp.getDomGreen()));
            blueLabel.setText(String.format("BLUE: %.1f%%", cOp.getDomBlue()));
            resultArea.setText(cOp.getLastHistogramText());
            resultArea.setCaretPosition(0);
        });

        if (!cOp.getLastHistogramText().isEmpty()) {
            redLabel.setText(String.format("RED: %.1f%%", cOp.getDomRed()));
            greenLabel.setText(String.format("GREEN: %.1f%%", cOp.getDomGreen()));
            blueLabel.setText(String.format("BLUE: %.1f%%", cOp.getDomBlue()));
            resultArea.setText(cOp.getLastHistogramText());
        }

        return mainPanel;
    }

    @Override
    protected void updateOperationInformation() {
        try {
            ColorHistogramOp cOp = (ColorHistogramOp) this.operation;
            int bins = Integer.parseInt(binCountField.getText().trim());
            cOp.updateOperation(bins);
        } catch (Exception ex) {
        }
    }
}