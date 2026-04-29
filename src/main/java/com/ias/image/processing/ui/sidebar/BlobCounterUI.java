package com.ias.image.processing.ui.sidebar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ias.image.processing.logic.operations.ImageOperation;
import com.ias.image.processing.logic.operations.BlobCounterOp;
import com.ias.image.processing.ui.MainFrame;

@SuppressWarnings("serial")
public class BlobCounterUI extends OperationUI {

    private JSlider minAreaSlider, maxAreaSlider;
    private JTextField minAreaField, maxAreaField;

    private JCheckBox drawBoxCheck, drawCenterCheck, drawTextCheck;
    private JTextField roiXF, roiYF, roiWF, roiHF;

    private JLabel totalLabel;
    private JLabel validLabel;

    private boolean isUpdating = false;
    private MainFrame myMainFrame;

    public BlobCounterUI(MainFrame mainFrame, ImageOperation operation, int index) {
        super(mainFrame, operation, index);
        this.myMainFrame = mainFrame;
    }

    @Override
    protected JPanel createParametersPanel(ImageOperation operation) {
        BlobCounterOp bOp = (BlobCounterOp) operation;

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JPanel filterPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        filterPanel.setBorder(new TitledBorder("Area Filters"));

        minAreaSlider = new JSlider(JSlider.HORIZONTAL, 0, 10000, (int)bOp.getMinArea());
        minAreaSlider.setMajorTickSpacing(2500);
        minAreaSlider.setPaintTicks(true);
        minAreaField = new JTextField(String.valueOf((int)bOp.getMinArea()), 5);
        filterPanel.add(createSliderRow("Min Area:", minAreaSlider, minAreaField));

        maxAreaSlider = new JSlider(JSlider.HORIZONTAL, 0, 500000, (int)bOp.getMaxArea());
        maxAreaSlider.setMajorTickSpacing(100000);
        maxAreaSlider.setPaintTicks(true);
        maxAreaField = new JTextField(String.valueOf((int)bOp.getMaxArea()), 5);
        filterPanel.add(createSliderRow("Max Area:", maxAreaSlider, maxAreaField));

        mainPanel.add(filterPanel);

        JPanel drawPanel = new JPanel(new GridLayout(3, 1, 2, 2));
        drawPanel.setBorder(new TitledBorder("Visuals"));

        drawBoxCheck = new JCheckBox("Bounding Box", bOp.isDrawBox());
        drawCenterCheck = new JCheckBox("Centroid", bOp.isDrawCentroid());
        drawTextCheck = new JCheckBox("Area Text", bOp.isDrawAreaText());

        java.awt.event.ActionListener boxListener = e -> triggerUpdate();
        drawBoxCheck.addActionListener(boxListener);
        drawCenterCheck.addActionListener(boxListener);
        drawTextCheck.addActionListener(boxListener);

        drawPanel.add(drawBoxCheck);
        drawPanel.add(drawCenterCheck);
        drawPanel.add(drawTextCheck);
        mainPanel.add(drawPanel);

        JPanel roiPanel = new JPanel(new GridLayout(2, 4, 2, 2));
        roiPanel.setBorder(new TitledBorder("ROI (0 for Full Image)"));

        roiXF = new JTextField(Integer.toString(bOp.getRoiX()), 4);
        roiYF = new JTextField(Integer.toString(bOp.getRoiY()), 4);
        roiWF = new JTextField(Integer.toString(bOp.getRoiW()), 4);
        roiHF = new JTextField(Integer.toString(bOp.getRoiH()), 4);

        java.awt.event.ActionListener roiListener = e -> triggerUpdate();
        roiXF.addActionListener(roiListener);
        roiYF.addActionListener(roiListener);
        roiWF.addActionListener(roiListener);
        roiHF.addActionListener(roiListener);

        roiPanel.add(new JLabel("X:")); roiPanel.add(roiXF);
        roiPanel.add(new JLabel("Y:")); roiPanel.add(roiYF);
        roiPanel.add(new JLabel("W:")); roiPanel.add(roiWF);
        roiPanel.add(new JLabel("H:")); roiPanel.add(roiHF);
        mainPanel.add(roiPanel);

        JPanel resultPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        resultPanel.setBorder(new TitledBorder("Live Count Results"));

        totalLabel = new JLabel("TOTAL BLOBS: 0");
        totalLabel.setForeground(Color.BLACK);

        validLabel = new JLabel("VALID OBJECTS: 0");
        validLabel.setForeground(Color.BLACK);
        validLabel.setFont(new Font(validLabel.getFont().getName(), Font.BOLD, 10));

        resultPanel.add(totalLabel);
        resultPanel.add(validLabel);
        mainPanel.add(resultPanel);

        // İşlem bittiğinde etiketleri günceller
        bOp.setOnResultUpdated(() -> {
            totalLabel.setText("TOTAL BLOBS: " + bOp.getLastTotalCount());
            validLabel.setText("VALID OBJECTS: " + bOp.getLastValidCount());
        });

        if (bOp.getLastTotalCount() > 0 || bOp.getLastValidCount() > 0) {
            totalLabel.setText("TOTAL BLOBS: " + bOp.getLastTotalCount());
            validLabel.setText("VALID OBJECTS: " + bOp.getLastValidCount());
        }

        setupSlider(minAreaSlider, minAreaField);
        setupSlider(maxAreaSlider, maxAreaField);

        return mainPanel;
    }

    private JPanel createSliderRow(String label, JSlider slider, JTextField field) {
        JPanel row = new JPanel(new BorderLayout(5, 0));
        row.add(new JLabel(label), BorderLayout.WEST);
        row.add(slider, BorderLayout.CENTER);
        row.add(field, BorderLayout.EAST);
        return row;
    }

    private void setupSlider(JSlider slider, JTextField field) {
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!isUpdating) {
                    try {
                        isUpdating = true;
                        field.setText(String.valueOf(slider.getValue()));
                        updateOperationInformation();

                        // Sürükleme bitince resmi günceller
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
                    if (val >= slider.getMinimum() && val <= slider.getMaximum()) {
                        slider.setValue(val);
                    }
                    field.setText(String.valueOf(val));
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
        try {
            BlobCounterOp bOp = (BlobCounterOp) this.operation;

            double minA = Double.parseDouble(minAreaField.getText().trim());
            double maxA = Double.parseDouble(maxAreaField.getText().trim());
            boolean dBox = drawBoxCheck.isSelected();
            boolean dCent = drawCenterCheck.isSelected();
            boolean dText = drawTextCheck.isSelected();

            int rx = Integer.parseInt(roiXF.getText().trim());
            int ry = Integer.parseInt(roiYF.getText().trim());
            int rw = Integer.parseInt(roiWF.getText().trim());
            int rh = Integer.parseInt(roiHF.getText().trim());

            bOp.updateOperation(minA, maxA, dBox, dCent, dText, rx, ry, rw, rh);
        } catch (Exception ex) {
        }
    }
}