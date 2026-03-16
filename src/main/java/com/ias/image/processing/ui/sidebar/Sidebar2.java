package com.ias.image.processing.ui.sidebar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.ias.image.processing.logic.ImageModel;
import com.ias.image.processing.logic.operations.ImageOperation;
import com.ias.image.processing.ui.MainFrame;

@SuppressWarnings("serial")
public class Sidebar2 extends JPanel {

	public MainFrame mainFrame;
	private JPanel mainPanel;

	public Sidebar2(MainFrame frame) {
		super(new BorderLayout());
		this.mainFrame = frame;

		this.setSize(new Dimension(250, 220));
		this.setPreferredSize(new Dimension(250, 220));
		this.setBorder(BorderFactory.createTitledBorder("Workflow History"));

		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		JScrollPane sp = new JScrollPane(mainPanel);
		this.add(sp);
	}

	public void rearrange() {

		mainPanel.removeAll();

		ImageModel model = this.mainFrame.getImageController().getModel();

		List<ImageOperation> operationsList = model.getOperations();

		for (int i = 0; i < operationsList.size(); i++) {

			ImageOperation operation = operationsList.get(i);

			JPanel operationPanel = createOperationPanelUI(operation, i);

			mainPanel.add(operationPanel);
		}

		mainPanel.add(Box.createVerticalGlue());
		mainPanel.add(Box.createVerticalGlue());
		mainPanel.revalidate();
		mainFrame.repaint();

	}

	private JPanel createOperationPanelUI(ImageOperation operation, int index) {

		OperationUI opUI = null;

		switch (operation.getOperationType()) {
		case CROP:

			opUI = new CropUI(mainFrame, operation, index);
			break;
		default:
			opUI = new OperationUI(mainFrame, operation, index);
		}

		return opUI;
	}

}
