package com.ias.image.processing.ui.sidebar;

import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.ias.image.processing.logic.operations.ImageOperation;
import com.ias.image.processing.logic.operations.RotateOp;
import com.ias.image.processing.ui.MainFrame;

@SuppressWarnings("serial")
public class RotateUI extends OperationUI {

	private MainFrame mainFrame;

	public RotateUI(MainFrame mainFrame, ImageOperation operation, int index) {
		super(mainFrame, operation, index);
		this.mainFrame = mainFrame;
	}

	@Override
	protected JPanel createParametersPanel(ImageOperation operation) {

		RotateOp rotateOp = (RotateOp) operation;

		JTextField angleField = new JTextField(Double.toString(rotateOp.getAngle()), 5);

		String[] hints = { "Bicubic", "Bilinear", "Nearest Neighbor" };
		JComboBox<String> hintBox = new JComboBox<>(hints);

		String currentHint = rotateOp.getHintName();
		if (currentHint != null && !currentHint.isEmpty()) {
			hintBox.setSelectedItem(currentHint);
		} else {
			hintBox.setSelectedItem("Bicubic");
		}

		ActionListener updateAction = e -> {
			try {
				double newAngle = Double.parseDouble(angleField.getText().trim());

				String newHintName = (String) hintBox.getSelectedItem();

				Object hintObject = RenderingHints.VALUE_INTERPOLATION_BICUBIC;
				if ("Bilinear".equals(newHintName)) {
					hintObject = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
				} else if ("Nearest Neighbor".equals(newHintName)) {
					hintObject = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
				}

				mainFrame.getImageController().updateOperation(getIndex(), new RotateOp(newAngle, hintObject, newHintName));

			} catch (Exception ex) {
			}
		};
		angleField.addActionListener(updateAction);
		hintBox.addActionListener(updateAction);

		JPanel panel = new JPanel(new GridLayout(2, 2));
		panel.add(new JLabel("Angle (degrees):"));
		panel.add(angleField);
		panel.add(new JLabel("Interpolation:"));
		panel.add(hintBox);

		return panel;
	}

	@Override
	protected void updateOperationInformation() {
		// TODO Auto-generated method stub

	}
}