package com.ias.image.processing.ui.sidebar;

import java.awt.GridLayout;
import java.awt.RenderingHints;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.ias.image.processing.logic.operations.ImageOperation;
import com.ias.image.processing.logic.operations.RotateOp;
import com.ias.image.processing.ui.MainFrame;

@SuppressWarnings("serial")
public class RotateUI extends OperationUI {

	private JTextField angleField;
	private JComboBox<String> hintBox;

	public RotateUI(MainFrame mainFrame, ImageOperation operation, int index) {
		super(mainFrame, operation, index);
	}

	@Override
	protected JPanel createParametersPanel(ImageOperation operation) {

		RotateOp rotateOp = (RotateOp) operation;
		angleField = new JTextField(Double.toString(rotateOp.getAngle()), 5);

		String[] hints = { "Bicubic", "Bilinear", "Nearest Neighbor" };
		hintBox = new JComboBox<>(hints);

		String currentHint = rotateOp.getHintName();
		if (currentHint != null && !currentHint.isEmpty()) {
			hintBox.setSelectedItem(currentHint);
		} else {
			hintBox.setSelectedItem("Bicubic");
		}

		angleField.addFocusListener(this);
		hintBox.addFocusListener(this);

		JPanel panel = new JPanel(new GridLayout(2, 2));
		panel.add(new JLabel("Angle (degrees):"));
		panel.add(angleField);
		panel.add(new JLabel("Interpolation:"));
		panel.add(hintBox);

		return panel;
	}

	@Override
	protected void updateOperationInformation() {

			double newAngle = Double.parseDouble(angleField.getText().trim());
			String newHintName = (String) hintBox.getSelectedItem();

			Object hintObject = RenderingHints.VALUE_INTERPOLATION_BICUBIC;
			if ("Bilinear".equals(newHintName)) {
				hintObject = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
			} else if ("Nearest Neighbor".equals(newHintName)) {
				hintObject = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
			}

			RotateOp rop = (RotateOp) this.operation;
			rop.updateOperation(newAngle,hintObject,newHintName);

		}
	}