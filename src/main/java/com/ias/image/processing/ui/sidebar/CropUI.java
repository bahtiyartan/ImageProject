package com.ias.image.processing.ui.sidebar;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.ias.image.processing.logic.operations.ImageOperation;
import com.ias.image.processing.logic.operations.CropOp;
import com.ias.image.processing.ui.MainFrame;

@SuppressWarnings("serial")
public class CropUI extends OperationUI {

	public CropUI(MainFrame mainFrame, ImageOperation operation, int index) {
		super(mainFrame, operation, index);
	}

	@Override
	protected JPanel createParametersPanel(ImageOperation operation) {
		JPanel panel = new JPanel(new GridLayout(4, 1, 2, 2));

		if (operation instanceof CropOp) {
			CropOp cropOp = (CropOp) operation;
			panel.add(new JLabel("Start x:" + cropOp.getX() +"px"));
			panel.add(new JLabel("Start Y: " + cropOp.getY() + " px"));
			panel.add(new JLabel("Width: " + cropOp.getWidth() + " px"));
			panel.add(new JLabel("Height: " + cropOp.getHeight() + " px"));
		} else {
			panel.add(new JLabel("  Error: Not a Crop Operation"));
		}

		return panel;
	}

}
