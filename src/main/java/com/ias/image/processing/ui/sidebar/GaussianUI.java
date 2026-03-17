package com.ias.image.processing.ui.sidebar;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.opencv.core.Core;

import com.ias.image.processing.logic.operations.GaussianBlurOp;
import com.ias.image.processing.logic.operations.ImageOperation;
import com.ias.image.processing.ui.MainFrame;

@SuppressWarnings("serial")
public class GaussianUI extends OperationUI {
	
	private JTextField kernelField;
	private JTextField sigmaField;
	private JComboBox<String> borderBox;
	
	public GaussianUI(MainFrame mainFrame, ImageOperation operation, int index) {
		super(mainFrame, operation, index);
	}

	@Override
	protected JPanel createParametersPanel(ImageOperation operation) {

		GaussianBlurOp blurOp = (GaussianBlurOp) operation;

		kernelField = new JTextField(Integer.toString(blurOp.getKernelSize()), 5);
		sigmaField = new JTextField(Double.toString(blurOp.getSigmaX()), 5);

		String[] borderNames = { "DEFAULT", "CONSTANT", "REPLICATE", "REFLECT" };
		borderBox = new JComboBox<>(borderNames);

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

		kernelField.addFocusListener(this);
		sigmaField.addFocusListener(this);
		borderBox.addActionListener(this);

		JPanel panel = new JPanel(new GridLayout(3, 2, 2, 2));
		panel.add(new JLabel("Kernel Size:"));
		panel.add(kernelField);
		panel.add(new JLabel("Sigma X:"));
		panel.add(sigmaField);
		panel.add(new JLabel("Border Type:"));
		panel.add(borderBox);

		return panel;
	}

	@Override
	protected void updateOperationInformation() {
		int newKernel = Integer.parseInt(kernelField.getText().trim());
		double newSigma = Double.parseDouble(sigmaField.getText().trim());

		String selectedBorder = (String) borderBox.getSelectedItem();

		int newBorderType = Core.BORDER_DEFAULT;
		if ("CONSTANT".equals(selectedBorder)) {
			newBorderType = Core.BORDER_CONSTANT;
		} else if ("REPLICATE".equals(selectedBorder)) {
			newBorderType = Core.BORDER_REPLICATE;
		} else if ("REFLECT".equals(selectedBorder)) {
			newBorderType = Core.BORDER_REFLECT;
		}
		
		GaussianBlurOp gop = (GaussianBlurOp) this.operation;

		gop.updateOperation(newKernel, newSigma, newBorderType);
		
	}
}