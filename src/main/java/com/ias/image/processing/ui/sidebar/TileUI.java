package com.ias.image.processing.ui.sidebar;

import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.ias.image.processing.logic.operations.ImageOperation;
import com.ias.image.processing.logic.operations.TileOp;
import com.ias.image.processing.ui.MainFrame;

@SuppressWarnings("serial")
public class TileUI extends OperationUI {

	private JTextField countXField;
	private JTextField countYField;
	private JTextField spacingXField;
	private JTextField spacingYField;

	public TileUI(MainFrame mainFrame, ImageOperation operation, int index) {
		super(mainFrame, operation, index);
	}

	@Override
	protected JPanel createParametersPanel(ImageOperation operation) {

		TileOp tileOp = (TileOp) operation;

		countXField = new JTextField(Integer.toString(tileOp.getCountX()), 5);
		countYField = new JTextField(Integer.toString(tileOp.getCountY()), 5);
		spacingXField = new JTextField(Integer.toString(tileOp.getSpacingX()), 5);
		spacingYField = new JTextField(Integer.toString(tileOp.getSpacingY()), 5);

		countXField.addFocusListener(this);
		countYField.addFocusListener(this);
		spacingXField.addFocusListener(this);
		spacingYField.addFocusListener(this);

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

	@Override
	protected void updateOperationInformation() {

		int cx = Integer.parseInt(countXField.getText());
		int cy = Integer.parseInt(countYField.getText());
		int sx = Integer.parseInt(spacingXField.getText());
		int sy = Integer.parseInt(spacingYField.getText());

		TileOp tileOperation = (TileOp) this.operation;
		tileOperation.update(cx, cy, sx, sy);
	}

}
