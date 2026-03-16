package com.ias.image.processing.ui.sidebar;

import java.awt.GridLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.opencv.core.Core;

import com.ias.image.processing.logic.operations.GaussianBlurOp;
import com.ias.image.processing.logic.operations.ImageOperation;
import com.ias.image.processing.ui.MainFrame;

public class GaussianUI extends OperationUI {

	public GaussianUI(MainFrame mainFrame, ImageOperation operation, int index) {
		super(mainFrame, operation, index);
	}

	@Override
	protected JPanel createParametersPanel(ImageOperation operation) {

		GaussianBlurOp blurOp = (GaussianBlurOp) operation;

		JTextField kernelField = new JTextField(Integer.toString(blurOp.getKernelSize()), 5);
		JTextField sigmaField = new JTextField(Double.toString(blurOp.getSigmaX()), 5);

		String[] borderNames = { "DEFAULT", "CONSTANT", "REPLICATE", "REFLECT" };
		JComboBox<String> borderBox = new JComboBox<>(borderNames);

		int currentBorder = blurOp.getBorderType();
		if (currentBorder == Core.BORDER_CONSTANT) {
			borderBox.setSelectedItem("CONSTANT");
		} else if (currentBorder == Core.BORDER_REPLICATE) {
			borderBox.setSelectedItem("REPLICATE");
		} else if (currentBorder == Core.BORDER_REFLECT) {
			borderBox.setSelectedItem("REFLECT");
		} else {
			borderBox.setSelectedItem("DEFAULT");
		}

		JPanel panel = new JPanel(new GridLayout(3, 2));
		panel.add(new JLabel("Kernel Size:"));
		panel.add(kernelField);
		panel.add(new JLabel("Sigma X:"));
		panel.add(sigmaField);
		panel.add(new JLabel("Border Type:"));
		panel.add(borderBox);

		// set combo value

		return panel;

	}

}
