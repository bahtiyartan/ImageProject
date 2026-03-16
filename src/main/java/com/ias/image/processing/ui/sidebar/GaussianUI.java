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

public class GaussianUI extends OperationUI {

	private MainFrame mainFrame;

	public GaussianUI(MainFrame mainFrame, ImageOperation operation, int index) {
		super(mainFrame, operation, index);
		this.mainFrame = mainFrame;
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

		ActionListener updateAction = e -> {
			try {
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

				mainFrame.getImageController().updateOperation(getIndex(), new GaussianBlurOp(newKernel, newSigma, newBorderType));

			} catch (Exception ex) {
			}
		};

		kernelField.addActionListener(updateAction);
		sigmaField.addActionListener(updateAction);
		borderBox.addActionListener(updateAction);

		JPanel panel = new JPanel(new GridLayout(3, 2));
		panel.add(new JLabel("Kernel Size:"));
		panel.add(kernelField);
		panel.add(new JLabel("Sigma X:"));
		panel.add(sigmaField);
		panel.add(new JLabel("Border Type:"));
		panel.add(borderBox);

		return panel;
	}
}