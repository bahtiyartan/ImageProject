package com.ias.image.processing.ui.sidebar;

import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.ias.image.processing.logic.operations.ImageOperation;
import com.ias.image.processing.logic.operations.CropOp;
import com.ias.image.processing.ui.MainFrame;

@SuppressWarnings("serial")
public class CropUI extends OperationUI {

	private JTextField xField;
	private JTextField yField;
	private JTextField widthField;
	private JTextField heightField;

	public CropUI(MainFrame mainFrame, ImageOperation operation, int index) {
		super(mainFrame, operation, index);
	}

	@Override
	protected JPanel createParametersPanel(ImageOperation operation) {

		CropOp cropOp = (CropOp) operation;

		xField = new JTextField(Integer.toString(cropOp.getX()), 5);
		yField = new JTextField(Integer.toString(cropOp.getY()), 5);
		widthField = new JTextField(Integer.toString(cropOp.getWidth()), 5);
		heightField = new JTextField(Integer.toString(cropOp.getHeight()), 5);

		xField.addFocusListener(this);
		yField.addFocusListener(this);
		widthField.addFocusListener(this);
		heightField.addFocusListener(this);

		JPanel panel = new JPanel(new GridLayout(4, 2, 2, 2));
		panel.add(new JLabel("Start X (px):"));
		panel.add(xField);
		panel.add(new JLabel("Start Y (px):"));
		panel.add(yField);
		panel.add(new JLabel("Width (px):"));
		panel.add(widthField);
		panel.add(new JLabel("Height (px):"));
		panel.add(heightField);

		return panel;
	}

	@Override
	protected void updateOperationInformation() {

			int newX = Integer.parseInt(xField.getText().trim());
			int newY = Integer.parseInt(yField.getText().trim());
			int newWidth = Integer.parseInt(widthField.getText().trim());
			int newHeight = Integer.parseInt(heightField.getText().trim());

			CropOp cop = (CropOp) this.operation;

			cop.updateOperation(newX, newY, newWidth, newHeight);

		}
	}
