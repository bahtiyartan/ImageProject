package com.ias.image.processing.ui;

import com.ias.image.processing.logic.operations.ImageOperation;
import com.ias.image.processing.logic.ImageController;
import com.ias.image.processing.logic.operations.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import org.opencv.core.Core;

public class Sidebar extends JPanel {
    private final DefaultListModel<ImageOperation> listModel = new DefaultListModel<>();
    private final JList<ImageOperation> list = new JList<>(listModel);
    private final ImageController controller;

    public Sidebar(ImageController controller) {
        this.controller = controller;

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
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this step?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                controller.removeOperation(index);
            }
        });

        menu.add(deleteItem);
        menu.show(invoker, x, y);
    }

    private void showOperationMenu(Component invoker) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem blurItem = new JMenuItem("Gaussian Blur");
        blurItem.addActionListener(e -> showGaussianBlurDialog(null, -1));
        JMenuItem cropItem = new JMenuItem("Crop");
        cropItem.addActionListener(e -> controller.setCropModeActive(true));
        JMenuItem rotateItem = new JMenuItem("Rotate");
        rotateItem.addActionListener(e -> showRotateDialog(null, -1));
        JMenuItem tileItem = new JMenuItem("Tile (Grid)");
        tileItem.addActionListener(e -> showTileDialog(null, -1));

        menu.add(blurItem); menu.add(cropItem); menu.add(rotateItem); menu.add(tileItem);
        menu.show(invoker, 0, -menu.getPreferredSize().height);
    }

    private void showRotateDialog(RotateOp existingOp, int index) {
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        JTextField angleField = new JTextField(existingOp != null ? String.valueOf(existingOp.getAngle()) : "");
        panel.add(new JLabel("Rotation Angle (Degree):"));
        panel.add(angleField);

        String[] qualityNames = {"Bicubic (High Quality)", "Bilinear (Medium)", "Nearest Neighbor (Fast)"};
        Object[] qualityHints = { RenderingHints.VALUE_INTERPOLATION_BICUBIC, RenderingHints.VALUE_INTERPOLATION_BILINEAR, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR };
        JComboBox<String> qualityCombo = new JComboBox<>(qualityNames);
        panel.add(new JLabel("Interpolation Method:"));
        panel.add(qualityCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Rotate Settings", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                double angle = Double.parseDouble(angleField.getText().trim());
                Object hint = qualityHints[qualityCombo.getSelectedIndex()];
                String hintName = qualityNames[qualityCombo.getSelectedIndex()].split(" ")[0];

                RotateOp newOp = new RotateOp(angle, hint, hintName);
                if (existingOp == null) controller.addOperation(newOp);
                else controller.updateOperation(index, newOp);
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Invalid input!"); }
        }
    }

    private void showGaussianBlurDialog(GaussianBlurOp existingOp, int index) {
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        JTextField kernelField = new JTextField(existingOp != null ? String.valueOf(existingOp.getKernelSize()) : "");
        panel.add(new JLabel("Kernel Size:"));
        panel.add(kernelField);
        JTextField sigmaField = new JTextField(existingOp != null ? String.valueOf(existingOp.getSigmaX()) : "");
        panel.add(new JLabel("Sigma X:"));
        panel.add(sigmaField);

        String[] borderNames = {"BORDER_DEFAULT", "BORDER_CONSTANT", "BORDER_REPLICATE", "BORDER_REFLECT"};
        JComboBox<String> borderCombo = new JComboBox<>(borderNames);
        panel.add(new JLabel("Border Type:"));
        panel.add(borderCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Gaussian Blur Settings", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int k = Integer.parseInt(kernelField.getText().trim());
                double s = Double.parseDouble(sigmaField.getText().trim());
                int b = existingOp != null ? existingOp.getBorderType() : Core.BORDER_DEFAULT;

                GaussianBlurOp newOp = new GaussianBlurOp(k, s, b);
                if (existingOp == null) controller.addOperation(newOp);
                else controller.updateOperation(index, newOp);
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Invalid input!"); }
        }
    }

    private void showTileDialog(TileOp existingOp, int index) {
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        JTextField cxF = new JTextField(existingOp != null ? String.valueOf(existingOp.getCountX()) : "");
        JTextField cyF = new JTextField(existingOp != null ? String.valueOf(existingOp.getCountY()) : "");
        JTextField sxF = new JTextField(existingOp != null ? String.valueOf(existingOp.getSpacingX()) : "");
        JTextField syF = new JTextField(existingOp != null ? String.valueOf(existingOp.getSpacingY()) : "");

        panel.add(new JLabel("Horizontal Count:")); panel.add(cxF);
        panel.add(new JLabel("Vertical Count:")); panel.add(cyF);
        panel.add(new JLabel("Horizontal Spacing:")); panel.add(sxF);
        panel.add(new JLabel("Vertical Spacing:")); panel.add(syF);

        int result = JOptionPane.showConfirmDialog(this, panel, "Tile Settings", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int cx = Integer.parseInt(cxF.getText().trim());
                int cy = Integer.parseInt(cyF.getText().trim());
                int sx = Integer.parseInt(sxF.getText().trim());
                int sy = Integer.parseInt(syF.getText().trim());

                TileOp newOp = new TileOp(cx, cy, sx, sy);
                if (existingOp == null) controller.addOperation(newOp);
                else controller.updateOperation(index, newOp);
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Invalid input!"); }
        }
    }

    public void updateList() {
        listModel.clear();
        for (ImageOperation op : controller.getModel().getOperations()) {
            listModel.addElement(op);
        }
    }
}