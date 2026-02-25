package com.ias.image.processing.ui;

import com.ias.image.processing.logic.ImageController;
import com.ias.image.processing.logic.operations.CropOp;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

public class MainFrame extends JFrame {

    private final Sidebar sidebar;
    private final JLabel display;
    private final ImageController controller;

    private Point startPoint;
    private Point currentPoint;
    private boolean isDragging = false;

    public MainFrame(ImageController controller) {
        this.controller = controller;
        this.controller.setUpdateViewCallback(this::refreshUI);

        setTitle("IAS IMAGE STUDIO");
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        display = new JLabel("Choose an Image", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (controller.isCropModeActive() && isDragging && startPoint != null && currentPoint != null) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    Stroke dashed = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
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

        sidebar = new Sidebar(controller);
        add(sidebar, BorderLayout.WEST);
        add(new JScrollPane(display), BorderLayout.CENTER);

        JButton loadBtn = new JButton("Select Image File");
        loadBtn.setMargin(new Insets(10, 10, 10, 10));
        loadBtn.addActionListener(e -> {
            JFileChooser jfc = new JFileChooser();
            if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    BufferedImage img = ImageIO.read(jfc.getSelectedFile());
                    controller.loadImage(img);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            }
        });
        add(loadBtn, BorderLayout.NORTH);

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
                if (!isDragging || !controller.isCropModeActive()) return;

                isDragging = false;
                BufferedImage img = controller.getModel().getCurrentImage();
                if (img == null || startPoint == null) return;

                int imgX = (display.getWidth() - img.getWidth()) / 2;
                int imgY = (display.getHeight() - img.getHeight()) / 2;

                Point endPoint = e.getPoint();

                int x = Math.min(startPoint.x, endPoint.x) - imgX;
                int y = Math.min(startPoint.y, endPoint.y) - imgY;
                int w = Math.abs(startPoint.x - endPoint.x);
                int h = Math.abs(startPoint.y - endPoint.y);

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

    private void refreshUI() {
        BufferedImage currentImg = controller.getModel().getCurrentImage();
        if (currentImg != null) {
            display.setIcon(new ImageIcon(currentImg));
            display.setText("");

            if (controller.isCropModeActive()) {
                display.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
            } else {
                display.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        }
        sidebar.updateList();
        display.revalidate();
        display.repaint();
    }
}