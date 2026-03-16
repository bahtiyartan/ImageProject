package com.ias.image.processing.ui.sidebar;

import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.ias.image.processing.logic.operations.ImageOperation;
import com.ias.image.processing.logic.operations.TileOp;
import com.ias.image.processing.ui.MainFrame;

public class TileUI extends OperationUI {

	private MainFrame mainFrame;

	public TileUI(MainFrame mainFrame, ImageOperation operation, int index) {
		super(mainFrame, operation, index);
		this.mainFrame = mainFrame;
	}

	@Override
	protected JPanel createParametersPanel(ImageOperation operation) {

		TileOp tileOp = (TileOp) operation;

		JTextField countXField = new JTextField(Integer.toString(tileOp.getCountX()), 5);
		JTextField countYField = new JTextField(Integer.toString(tileOp.getCountY()), 5);
		JTextField spacingXField = new JTextField(Integer.toString(tileOp.getSpacingX()), 5);
		JTextField spacingYField = new JTextField(Integer.toString(tileOp.getSpacingY()), 5);

		java.awt.event.ActionListener updateAction = e -> {
			try {
				int cx = Integer.parseInt(countXField.getText());
				int cy = Integer.parseInt(countYField.getText());
				int sx = Integer.parseInt(spacingXField.getText());
				int sy = Integer.parseInt(spacingYField.getText());

				mainFrame.getImageController().updateOperation(getIndex(), new TileOp(cx, cy, sx, sy));
			} catch (Exception ex) {
			}
		};
		countXField.addActionListener(updateAction);
		countYField.addActionListener(updateAction);
		spacingXField.addActionListener(updateAction);
		spacingYField.addActionListener(updateAction);

		JPanel panel = new JPanel(new GridLayout(4, 2));
		panel.add(new JLabel("Columns (X count):"));
		panel.add(countXField);
		panel.add(new JLabel("Rows (Y count):"));
		panel.add(countYField);
		panel.add(new JLabel("Horizontal Spacing:"));
		panel.add(spacingXField);
		panel.add(new JLabel("Vertical Spacing:"));
		panel.add(spacingYField);

		return panel;

	}

}
