package com.ias.image.processing.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import com.ias.image.processing.logic.ImageController;
import com.ias.image.processing.logic.operations.CropOp;
import com.ias.image.processing.logic.operations.GaussianBlurOp;
import com.ias.image.processing.logic.operations.ImageOperation;
import com.ias.image.processing.logic.operations.OperationType;
import com.ias.image.processing.logic.operations.RotateOp;
import com.ias.image.processing.logic.operations.TileOp;
import com.ias.image.processing.ui.sidebar.AddOperationAction;

@SuppressWarnings("serial")
public class Sidebar extends JPanel {

	private final DefaultListModel<ImageOperation> listModel = new DefaultListModel<>();
	private final JList<ImageOperation> list = new JList<>(listModel);
	private final ImageController controller;
	private MainFrame MainFrame;

	public Sidebar(MainFrame frame, ImageController controller) {
		this.controller = controller;
		this.MainFrame = frame;

		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(250, 0));
		setBorder(BorderFactory.createTitledBorder("Workflow History"));

		list.setCellRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (value instanceof ImageOperation) {
					setText(((ImageOperation) value).getOperationName());
				}
				return this;
			}
		});

		list.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int index = list.locationToIndex(e.getPoint());
					if (index != -1) {
						editOperation(index);
					}
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					int index = list.locationToIndex(e.getPoint());
					if (index != -1) {
						list.setSelectedIndex(index);
						showDeletePopupMenu(e.getComponent(), e.getX(), e.getY(), index);
					}
				}
			}
		});

		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setBackground(new Color(245, 245, 245));
		add(new JScrollPane(list), BorderLayout.CENTER);

		JPanel btnPanel = new JPanel(new GridLayout(1, 2, 5, 5));
		JButton undoBtn = new JButton("Undo");
		undoBtn.addActionListener(e -> controller.undo());

		JButton addBtn = new JButton("+ Add Step");
		addBtn.addActionListener(e -> showOperationMenu(addBtn));

		btnPanel.add(undoBtn);
		btnPanel.add(addBtn);
		add(btnPanel, BorderLayout.SOUTH);
	}

	private void editOperation(int index) {
		ImageOperation op = listModel.get(index);

		if (op instanceof GaussianBlurOp) {
			showGaussianBlurDialog((GaussianBlurOp) op, index);
		} else if (op instanceof RotateOp) {
			showRotateDialog((RotateOp) op, index);
		} else if (op instanceof TileOp) {
			showTileDialog((TileOp) op, index);
		} else if (op instanceof CropOp) {
			JOptionPane.showMessageDialog(this, "Crop operations are handled via mouse on the image.");
		}
	}

	private void showDeletePopupMenu(Component invoker, int x, int y, int index) {
		JPopupMenu menu = new JPopupMenu();
		JMenuItem deleteItem = new JMenuItem("Delete Step");
		deleteItem.setForeground(Color.RED);

		deleteItem.addActionListener(e -> {
			int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this step?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

			if (confirm == JOptionPane.YES_OPTION) {
				controller.removeOperation(index);
			}
		});

		menu.add(deleteItem);
		menu.show(invoker, x, y);
	}

	private void showOperationMenu(Component invoker) {
		JPopupMenu menu = new JPopupMenu();
		JMenuItem blurItem = new JMenuItem(new AddOperationAction(MainFrame, "Gaussian Blur", OperationType.GAUSSIANBLUR));
		JMenuItem rotateItem = new JMenuItem(new AddOperationAction(MainFrame, "Rotate", OperationType.ROTATE));
		JMenuItem tileItem = new JMenuItem(new AddOperationAction(MainFrame, "Tile (Grid)", OperationType.TILE));
		JMenuItem histItem = new JMenuItem(new AddOperationAction(MainFrame, "Color Histogram", OperationType.COLOR_HISTOGRAM));

		JMenuItem cropItem = new JMenuItem("Crop");
		cropItem.addActionListener(e -> controller.setCropModeActive(true));

		menu.add(cropItem);
		menu.add(rotateItem);
		menu.add(tileItem);
		menu.addSeparator();
		menu.add(blurItem);
		menu.addSeparator();
		menu.add(histItem);
		menu.show(invoker, 0, -menu.getPreferredSize().height);
	}

	private void showRotateDialog(RotateOp existingOp, int index) {
		JTextField angleField = new JTextField("50", 5);

		String[] hints = { "Bicubic", "Bilinear", "Nearest Neighbor" };
		JComboBox<String> hintBox = new JComboBox<>(hints);
		hintBox.setSelectedItem("Bicubic");

		JPanel panel = new JPanel(new GridLayout(2, 2));
		panel.add(new JLabel("Angle (degrees):"));
		panel.add(angleField);
		panel.add(new JLabel("Interpolation:"));
		panel.add(hintBox);

		String[] qualityNames = { "Bicubic (High Quality)", "Bilinear (Medium)", "Nearest Neighbor (Fast)" };
		Object[] qualityHints = { RenderingHints.VALUE_INTERPOLATION_BICUBIC, RenderingHints.VALUE_INTERPOLATION_BILINEAR, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR };
		JComboBox<String> qualityCombo = new JComboBox<>(qualityNames);
		panel.add(new JLabel("Interpolation Method:"));
		panel.add(qualityCombo);

		int result = JOptionPane.showConfirmDialog(this, panel, "Rotate Settings", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (result == JOptionPane.OK_OPTION) {
			try {
				double angle = Double.parseDouble(angleField.getText().trim());
				String hint = (String) hintBox.getSelectedItem();

				Object hintObj = java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC;
				if ("Bilinear".equals(hint))
					hintObj = java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR;
				else if ("Nearest Neighbor".equals(hint))
					hintObj = java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;

				RotateOp newOp = new RotateOp(angle, hintObj, hint);
				if (existingOp == null)
					controller.addOperation(newOp);
				else
					controller.updateOperation(index, newOp);
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(this, "Please enter a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void showGaussianBlurDialog(GaussianBlurOp existingOp, int index) {
		JTextField kernelField = new JTextField("20", 5);
		JTextField sigmaField = new JTextField("2", 5);

		String[] borderNames = { "DEFAULT", "CONSTANT", "REPLICATE", "REFLECT" };
		JComboBox<String> borderBox = new JComboBox<>(borderNames);
		borderBox.setSelectedItem("DEFAULT");

		JPanel panel = new JPanel(new GridLayout(3, 2));
		panel.add(new JLabel("Kernel Size:"));
		panel.add(kernelField);
		panel.add(new JLabel("Sigma X:"));
		panel.add(sigmaField);
		panel.add(new JLabel("Border Type:"));
		panel.add(borderBox);

		int result = JOptionPane.showConfirmDialog(this, panel, "Gaussian Blur Settings", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (result == JOptionPane.OK_OPTION) {
			try {
				int k = Integer.parseInt(kernelField.getText().trim());
				double sX = Double.parseDouble(sigmaField.getText().trim());
				String bName = (String) borderBox.getSelectedItem();

				int bType = org.opencv.core.Core.BORDER_DEFAULT;
				if ("CONSTANT".equals(bName))
					bType = org.opencv.core.Core.BORDER_CONSTANT;
				else if ("REPLICATE".equals(bName))
					bType = org.opencv.core.Core.BORDER_REPLICATE;
				else if ("REFLECT".equals(bName))
					bType = org.opencv.core.Core.BORDER_REFLECT;

				GaussianBlurOp newOp = new GaussianBlurOp(k, sX, bType);
				if (existingOp == null)
					controller.addOperation(newOp);
				else
					controller.updateOperation(index, newOp);
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(this, "Please enter valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void showTileDialog(TileOp existingOp, int index) {
		JTextField countXField = new JTextField("2", 5);
		JTextField countYField = new JTextField("2", 5);
		JTextField spacingXField = new JTextField("2", 5);
		JTextField spacingYField = new JTextField("2", 5);

		JPanel panel = new JPanel(new GridLayout(4, 2));
		panel.add(new JLabel("Columns (X count):"));
		panel.add(countXField);
		panel.add(new JLabel("Rows (Y count):"));
		panel.add(countYField);
		panel.add(new JLabel("Horizontal Spacing:"));
		panel.add(spacingXField);
		panel.add(new JLabel("Vertical Spacing:"));
		panel.add(spacingYField);

		int result = JOptionPane.showConfirmDialog(this, panel, "Tile Settings", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (result == JOptionPane.OK_OPTION) {
			try {
				int cx = Integer.parseInt(countXField.getText().trim());
				int cy = Integer.parseInt(countYField.getText().trim());
				int sx = Integer.parseInt(spacingXField.getText().trim());
				int sy = Integer.parseInt(spacingYField.getText().trim());

				TileOp newOp = new TileOp(cx, cy, sx, sy);
				if (existingOp == null)
					controller.addOperation(newOp);
				else
					controller.updateOperation(index, newOp);
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(this, "Please enter valid integers.", "Input Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public void updateList() {
		listModel.clear();
		for (ImageOperation op : controller.getModel().getOperations()) {
			listModel.addElement(op);
		}
	}
}