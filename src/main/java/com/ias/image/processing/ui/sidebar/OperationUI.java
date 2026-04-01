package com.ias.image.processing.ui.sidebar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.ias.image.processing.logic.operations.ImageOperation;
import com.ias.image.processing.ui.MainFrame;

@SuppressWarnings("serial")
public abstract class OperationUI extends JPanel implements FocusListener, ActionListener {

    private MainFrame MainFrame;
    private int index;
    protected ImageOperation operation;

    public OperationUI(MainFrame mainFrame, ImageOperation operation, int index) {
        super(new BorderLayout());

        this.index = index;
        this.operation = operation;
        this.MainFrame = mainFrame;

        DeleteOperationAction deleteOperation = new DeleteOperationAction(mainFrame, operation, index);

        this.setBackground(Color.white);
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(true);
        header.setBackground(Color.LIGHT_GRAY);

        JPanel rightControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));
        rightControls.setOpaque(false);

        JButton upButton = new JButton("▲");
        upButton.setFocusable(false);
        upButton.setPreferredSize(new Dimension(24, 24));
        upButton.setBorder(null);
        upButton.setBackground(Color.LIGHT_GRAY);
        upButton.setEnabled(index > 0);
        upButton.addActionListener(e -> {
            mainFrame.controller.moveOperationUp(this.index);
        });

        JButton downButton = new JButton("▼");
        downButton.setFocusable(false);
        downButton.setPreferredSize(new Dimension(24, 24));
        downButton.setBorder(null);
        downButton.setBackground(Color.LIGHT_GRAY);
        int totalOps = mainFrame.controller.getModel().getOperations().size();
        downButton.setEnabled(index < totalOps - 1);
        downButton.addActionListener(e -> {
            mainFrame.controller.moveOperationDown(this.index);
        });

        JCheckBox activeCheck = new JCheckBox();
        activeCheck.setOpaque(false);
        activeCheck.setSelected(operation.isActive());
        activeCheck.setToolTipText("Enable/Disable Operation");
        activeCheck.addActionListener(e -> {
            this.operation.setActive(activeCheck.isSelected());
            this.MainFrame.controller.processImage();
        });

        JButton deleteButton = new JButton(deleteOperation);
        deleteButton.setFocusable(false);
        deleteButton.setPreferredSize(new Dimension(24, 24));
        deleteButton.setBorder(null);
        deleteButton.setBackground(Color.LIGHT_GRAY);

        rightControls.add(upButton);
        rightControls.add(downButton);
        rightControls.add(activeCheck);
        rightControls.add(deleteButton);
        header.add(rightControls, BorderLayout.EAST);

        JLabel headerLabel = new JLabel(operation.getOperationType().getDescription());
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 2, 2, 0));
        header.add(headerLabel, BorderLayout.CENTER);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        this.add(header, BorderLayout.NORTH);

        JPanel opeartionUI = this.createParametersPanel(operation);
        opeartionUI.setOpaque(true);
        opeartionUI.setBackground(Color.WHITE);

        this.add(opeartionUI, BorderLayout.CENTER);

        this.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.setMaximumSize(new Dimension(Integer.MAX_VALUE, this.getPreferredSize().height));
        this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2), BorderFactory.createLineBorder(Color.GRAY)));
        this.setBackground(Color.WHITE);
    }

    public int getIndex() {
        return this.index;
    }

    protected abstract JPanel createParametersPanel(ImageOperation operation);

    protected abstract void updateOperationInformation();

    @Override
    public void focusGained(FocusEvent e) {
    }

    @Override
    public void focusLost(FocusEvent e) {
        this.updateOperationInformation();
        this.MainFrame.controller.processImage();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.updateOperationInformation();
        this.MainFrame.controller.processImage();
    }
}