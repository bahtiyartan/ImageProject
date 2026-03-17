package com.ias.image.processing.ui.sidebar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
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
		JButton deleteButton = new JButton(deleteOperation);
		deleteButton.setFocusable(false);
		deleteButton.setPreferredSize(new Dimension(24, 24));
		deleteButton.setBorder(null);
		deleteButton.setBackground(Color.LIGHT_GRAY);

		header.add(deleteButton, BorderLayout.EAST);
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
		// do nothing intentionally
	}

	@Override
	public void focusLost(FocusEvent e) {
		this.updateOperationInformation();
		this.MainFrame.controller.processImage();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.updateOperationInformation();
	}
}
