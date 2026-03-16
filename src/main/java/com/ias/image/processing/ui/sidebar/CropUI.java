package com.ias.image.processing.ui.sidebar;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.ias.image.processing.logic.operations.ImageOperation;
import com.ias.image.processing.ui.MainFrame;

@SuppressWarnings("serial")
public class CropUI extends OperationUI {

	public CropUI(MainFrame mainFrame, ImageOperation operation, int index) {
		super(mainFrame, operation, index);
	}

	@Override
	protected JPanel createParametersPanel(ImageOperation operation) {
		JPanel panel = new JPanel(new BorderLayout());

		panel.add(new JLabel("crop params"));

		return panel;
	}

}
