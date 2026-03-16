package com.ias.image.processing.ui.sidebar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.ias.image.processing.logic.operations.ImageOperation;
import com.ias.image.processing.ui.MainFrame;

@SuppressWarnings("serial")
public class OperationUI extends JPanel {

	private MainFrame MainFrame;
	private int index;

	public OperationUI(MainFrame mainFrame, ImageOperation operation, int index) {
		super(new BorderLayout());

		this.index = index;
		DeleteOperationAction deleteOperation = new DeleteOperationAction(mainFrame, operation, index);

		this.setBackground(Color.white);
		JPanel header = new JPanel(new BorderLayout());
		header.setOpaque(false);
		JButton deleteButton = new JButton(deleteOperation);
		deleteButton.setFocusable(false);
		deleteButton.setPreferredSize(new Dimension(24, 24));
		deleteButton.setBorder(null);
		deleteButton.setBackground(Color.white);

		header.add(deleteButton, BorderLayout.EAST);
		JLabel headerLabel = new JLabel(operation.getOperationType().getDescription());
		headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 2, 2, 0));
		header.add(headerLabel, BorderLayout.CENTER);
		header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
		this.add(header, BorderLayout.NORTH);

		JPanel opeartionUI = this.createParametersPanel(operation);

		this.add(opeartionUI, BorderLayout.CENTER);

		this.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.setMaximumSize(new Dimension(Integer.MAX_VALUE, this.getPreferredSize().height));
		this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2), BorderFactory.createLineBorder(Color.GRAY)));
		this.setBackground(Color.WHITE);
	}

	public int getIndex() {
		return this.index;
	}
	protected JPanel createParametersPanel(ImageOperation operation) {
		JPanel panel = new JPanel(new BorderLayout());

		panel.add(new JLabel("default operation ui"));

		return panel;
	}
}
