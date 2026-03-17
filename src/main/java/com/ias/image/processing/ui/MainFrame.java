package com.ias.image.processing.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.ias.image.processing.logic.ImageController;
import com.ias.image.processing.logic.operations.CropOp;
import com.ias.image.processing.ui.sidebar.Sidebar2;
import com.ias.util.SimpleFileHandler;

public class MainFrame extends JFrame {

	public final Sidebar2 sidebar2;
	private final JLabel display;
	public final ImageController controller;

	private Point startPoint;
	private Point currentPoint;
	private boolean isDragging = false;
	private double currentScale = 1.0;

	public MainFrame(ImageController controller) {
		this.controller = controller;
		this.controller.setUpdateViewCallback(this::refreshUI);

		setTitle("Image Toolkit");
		setSize(1200, 800);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		JButton loadBtn = new JButton("Select Image File");
		loadBtn.setPreferredSize(new Dimension(0, 40));
		loadBtn.addActionListener(e -> {
			JFileChooser jfc = new JFileChooser(SimpleFileHandler.getDataFolder());
			if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				try {
					BufferedImage img = ImageIO.read(jfc.getSelectedFile());
					controller.loadImage(jfc.getSelectedFile());
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
				}
			}
		});
		add(loadBtn, BorderLayout.NORTH);

		JPanel leftContainer = new JPanel(new BorderLayout());
		leftContainer.setPreferredSize(new Dimension(320, 0));

		JPanel projectButtonsPanel = new JPanel(new GridLayout(1, 3, 2, 2));
		JButton saveProjBtn = new JButton("Save Project");
		JButton loadProjBtn = new JButton("Open Project");
		JButton showResultsBtn = new JButton("Show Results");

		saveProjBtn.setFont(new Font("Arial", Font.PLAIN, 11));
		loadProjBtn.setFont(new Font("Arial", Font.PLAIN, 11));
		showResultsBtn.setFont(new Font("Arial", Font.PLAIN, 11));

		projectButtonsPanel.add(saveProjBtn);
		projectButtonsPanel.add(loadProjBtn);
		projectButtonsPanel.add(showResultsBtn);
		leftContainer.add(projectButtonsPanel, BorderLayout.NORTH);

		sidebar2 = new Sidebar2(this);
		leftContainer.add(sidebar2, BorderLayout.CENTER);

		add(leftContainer, BorderLayout.WEST);

		display = new JLabel("", SwingConstants.CENTER) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (controller.isCropModeActive() && isDragging && startPoint != null && currentPoint != null) {
					Graphics2D g2d = (Graphics2D) g.create();
					Stroke dashed = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
							new float[] { 9 }, 0);
					g2d.setStroke(dashed);
					g2d.setColor(Color.RED);

					int x = Math.min(startPoint.x, currentPoint.x);
					int y = Math.min(startPoint.y, currentPoint.y);
					int width = Math.abs(startPoint.x - currentPoint.x);
					int height = Math.abs(startPoint.y - currentPoint.y);

					g2d.drawRect(x, y, width, height);
					g2d.dispose();
				}
			}
		};

		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.setBackground(Color.LIGHT_GRAY);
		centerPanel.add(display, BorderLayout.CENTER);
		add(centerPanel, BorderLayout.CENTER);

		saveProjBtn.addActionListener(e -> {
			JFileChooser jfc = new JFileChooser();
			if (jfc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				try {
					controller.saveProject(jfc.getSelectedFile());
					JOptionPane.showMessageDialog(this, "Project Saved!");
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(this, "Save Error: " + ex.getMessage());
				}
			}
		});

		loadProjBtn.addActionListener(e -> {
			JFileChooser jfc = new JFileChooser();
			if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				try {
					controller.loadProject(jfc.getSelectedFile());
					sidebar2.rearrange();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(this, "Load Error: " + ex.getMessage());
				}
			}
		});

		showResultsBtn.addActionListener(e -> {
			var res = controller.getModel().getCurrentResult();

			if (res != null && (res.hasString() || res.hasInteger() || res.hasDouble())) {
				StringBuilder resultText = new StringBuilder("Analysis Results\n\n");

				if (res.hasString()) resultText.append(res.getStringResult()).append("\n");
				if (res.hasInteger()) resultText.append("Count: ").append(res.getIntResult()).append("\n");
				if (res.hasDouble()) resultText.append("Value: ").append(String.format("%.2f", res.getDoubleResult())).append("\n");

				JOptionPane.showMessageDialog(this,
						resultText.toString(),
						"Process Results",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(this,
						"No specific data (String, Integer, Double) found for the current operation.\nThis operation only outputs an Image.",
						"No Results",
						JOptionPane.WARNING_MESSAGE);
			}
		});

		setupMouseListeners();
	}

	private void setupMouseListeners() {
		display.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (controller.isCropModeActive()) {
					startPoint = e.getPoint();
					isDragging = true;
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (!isDragging || !controller.isCropModeActive())
					return;
				isDragging = false;
				BufferedImage realImg = controller.getModel().getCurrentImage();
				if (realImg == null || startPoint == null)
					return;
				Icon icon = display.getIcon();
				if (icon == null)
					return;
				int iconW = icon.getIconWidth();
				int iconH = icon.getIconHeight();
				int offsetX = (display.getWidth() - iconW) / 2;
				int offsetY = (display.getHeight() - iconH) / 2;

				Point endPoint = e.getPoint();
				int x = (int) ((Math.min(startPoint.x, endPoint.x) - offsetX) / currentScale);
				int y = (int) ((Math.min(startPoint.y, endPoint.y) - offsetY) / currentScale);
				int w = (int) (Math.abs(startPoint.x - endPoint.x) / currentScale);
				int h = (int) (Math.abs(startPoint.y - endPoint.y) / currentScale);
				if (w > 5 && h > 5) {
					controller.addOperation(new CropOp(x, y, w, h));
					controller.setCropModeActive(false);
				}
				display.repaint();
			}
		});
		display.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if (controller.isCropModeActive()) {
					currentPoint = e.getPoint();
					display.repaint();
				}
			}
		});
	}

	public void refreshUI() {
		BufferedImage currentImg = controller.getModel().getCurrentImage();
		if (currentImg != null) {
			display.setIcon(getScaledImageIcon(currentImg));
			display.setText("");
			display.setCursor(
					new Cursor(controller.isCropModeActive() ? Cursor.CROSSHAIR_CURSOR : Cursor.DEFAULT_CURSOR));
		}
		display.revalidate();
		display.repaint();

		sidebar2.rearrange();
	}

	private ImageIcon getScaledImageIcon(BufferedImage srcImg) {
		int maxWidth = display.getParent().getWidth();
		int maxHeight = display.getParent().getHeight();
		if (maxWidth <= 50 || maxHeight <= 50)
			return new ImageIcon(srcImg);
		int srcW = srcImg.getWidth();
		int srcH = srcImg.getHeight();
		double widthRatio = (double) maxWidth / srcW;
		double heightRatio = (double) maxHeight / srcH;
		currentScale = Math.min(widthRatio, heightRatio);
		if (currentScale > 1.0)
			currentScale = 1.0;
		int newW = (int) (srcW * currentScale);
		int newH = (int) (srcH * currentScale);
		Image scaledImg = srcImg.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		return new ImageIcon(scaledImg);
	}

	public ImageController getImageController() {
		return this.controller;
	}
}