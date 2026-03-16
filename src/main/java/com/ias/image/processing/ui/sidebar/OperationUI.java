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

	private MainFrame mainFrame;

	public OperationUI(MainFrame mainFrame, ImageOperation operation, int index) {
		super(new BorderLayout());

		DeleteOperationAction deleteOperation = new DeleteOperationAction(mainFrame, operation, index);

		this.setBackground(Color.white);
		JPanel header = new JPanel(new BorderLayout());
		header.setOpaque(false);
		JButton closeButton = new JButton(deleteOperation);
		closeButton.setFocusable(false);
		closeButton.setPreferredSize(new Dimension(24, 24));

		header.add(closeButton, BorderLayout.EAST);
		header.add(new JLabel(operation.getOperationType().toString()), BorderLayout.CENTER);
		this.add(header, BorderLayout.NORTH);

		JPanel opeartionUI = this.createParametersPanel(operation);

		this.add(opeartionUI, BorderLayout.CENTER);

		this.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.setMaximumSize(new Dimension(Integer.MAX_VALUE, this.getPreferredSize().height));
		this.setBorder(BorderFactory.createTitledBorder(""));
	}

	protected JPanel createParametersPanel(ImageOperation operation) {
		JPanel panel = new JPanel(new BorderLayout());

		panel.add(new JLabel("default operation ui"));

		return panel;
	}
}
