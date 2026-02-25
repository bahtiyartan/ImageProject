package com.ias.image.processing.ui;

import com.ias.image.processing.logic.operations.ImageOperation;
import com.ias.image.processing.logic.ImageController;
import com.ias.image.processing.logic.operations.*;
import javax.swing.*;
import java.awt.*;

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

    private void showOperationMenu(Component invoker) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem cropItem = new JMenuItem("Crop");
        cropItem.addActionListener(e -> controller.setCropModeActive(true));

        JMenuItem rotateItem = new JMenuItem("Rotate");
        rotateItem.addActionListener(e -> {
            String val = JOptionPane.showInputDialog(this, "Angle:", "0");
            if (val != null && !val.isEmpty()) {
                controller.addOperation(new RotateOp(Double.parseDouble(val)));
            }
        });

        JMenuItem tileItem = new JMenuItem("Tile");
        tileItem.addActionListener(e -> {
            String val = JOptionPane.showInputDialog(this, "Repeat Count:");
            if (val != null && !val.isEmpty()) {
                controller.addOperation(new TileOp(Integer.parseInt(val)));
            }
        });

        menu.add(cropItem);
        menu.add(rotateItem);
        menu.add(tileItem);

        menu.show(invoker, 0, -menu.getPreferredSize().height);
    }

    public void updateList() {
        listModel.clear();
        for (ImageOperation op : controller.getModel().getOperations()) {
            listModel.addElement(op);
        }
    }
}