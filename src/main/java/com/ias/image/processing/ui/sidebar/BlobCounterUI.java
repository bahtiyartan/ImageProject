package com.ias.image.processing.ui.sidebar;

import java.awt.GridLayout;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.ias.image.processing.logic.operations.ImageOperation;
import com.ias.image.processing.logic.operations.BlobCounterOp;
import com.ias.image.processing.ui.MainFrame;

@SuppressWarnings("serial")
public class BlobCounterUI extends OperationUI {

    private JTextField minAreaField, maxAreaField;
    private JCheckBox drawBoxCheck, drawCenterCheck, drawTextCheck;
    private JTextField roiXF, roiYF, roiWF, roiHF;

    public BlobCounterUI(MainFrame mainFrame, ImageOperation operation, int index) {
        super(mainFrame, operation, index);
    }

    @Override
    protected JPanel createParametersPanel(ImageOperation operation) {
        BlobCounterOp bOp = (BlobCounterOp) operation;

        minAreaField = new JTextField(Double.toString(bOp.getMinArea()), 5);
        maxAreaField = new JTextField(Double.toString(bOp.getMaxArea()), 5);
        minAreaField.addFocusListener(this);
        maxAreaField.addFocusListener(this);

        drawBoxCheck = new JCheckBox("Bounding Box", bOp.isDrawBox());
        drawCenterCheck = new JCheckBox("Centroid", bOp.isDrawCentroid());
        drawTextCheck = new JCheckBox("Area Text", bOp.isDrawAreaText());
        drawBoxCheck.addActionListener(this);
        drawCenterCheck.addActionListener(this);
        drawTextCheck.addActionListener(this);

        roiXF = new JTextField(Integer.toString(bOp.getRoiX()), 4);
        roiYF = new JTextField(Integer.toString(bOp.getRoiY()), 4);
        roiWF = new JTextField(Integer.toString(bOp.getRoiW()), 4);
        roiHF = new JTextField(Integer.toString(bOp.getRoiH()), 4);
        roiXF.addFocusListener(this);
        roiYF.addFocusListener(this);
        roiWF.addFocusListener(this);
        roiHF.addFocusListener(this);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JPanel filterPanel = new JPanel(new GridLayout(2, 2, 2, 2));
        filterPanel.setBorder(new TitledBorder("Area Filters"));
        filterPanel.add(new JLabel("Min Area:")); filterPanel.add(minAreaField);
        filterPanel.add(new JLabel("Max Area:")); filterPanel.add(maxAreaField);
        mainPanel.add(filterPanel);

        JPanel drawPanel = new JPanel(new GridLayout(3, 1, 2, 2));
        drawPanel.setBorder(new TitledBorder("Visuals"));
        drawPanel.add(drawBoxCheck);
        drawPanel.add(drawCenterCheck);
        drawPanel.add(drawTextCheck);
        mainPanel.add(drawPanel);

        JPanel roiPanel = new JPanel(new GridLayout(2, 4, 2, 2));
        roiPanel.setBorder(new TitledBorder("ROI (0 for Full Image)"));
        roiPanel.add(new JLabel("X:")); roiPanel.add(roiXF);
        roiPanel.add(new JLabel("Y:")); roiPanel.add(roiYF);
        roiPanel.add(new JLabel("W:")); roiPanel.add(roiWF);
        roiPanel.add(new JLabel("H:")); roiPanel.add(roiHF);
        mainPanel.add(roiPanel);

        return mainPanel;
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