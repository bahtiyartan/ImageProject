package com.ias.image.processing.ui.sidebar;

import java.awt.GridLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.ias.image.processing.logic.operations.GaussianBlurOp;
import com.ias.image.processing.logic.operations.ImageOperation;
import com.ias.image.processing.ui.MainFrame;

@SuppressWarnings("serial")
public class GaussianUI extends OperationUI {

	public GaussianUI(MainFrame mainFrame, ImageOperation operation, int index) {
		super(mainFrame, operation, index);
	}

	@Override
	protected JPanel createParametersPanel(ImageOperation operation) {

		JTextField kernelField = new JTextField("20", 5);
		JTextField sigmaField = new JTextField("2", 5);

		String[] borderNames = { "DEFAULT", "CONSTANT", "REPLICATE", "REFLECT" };
		JComboBox<String> borderBox = new JComboBox<>(borderNames);
		borderBox.setSelectedItem("DEFAULT");

		JPanel panel = new JPanel(new GridLayout(3, 2));
		panel.add(new JLabel("Kernel Size:"));
		panel.add(kernelField);
		panel.add(new JLabel("Sigma X:"));
		panel.add(sigmaField);
		panel.add(new JLabel("Border Type:"));
		panel.add(borderBox);

		GaussianBlurOp gbop = (GaussianBlurOp) operation;

		kernelField.setText(Integer.toString(gbop.getKernelSize()));
		sigmaField.setText(Double.toString(gbop.getSigmaX()));

		// set combo value

		return panel;

	}

}
