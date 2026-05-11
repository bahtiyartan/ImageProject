package com.ias.image.processing.ui.sidebar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import com.ias.image.processing.logic.ImageModel;
import com.ias.image.processing.logic.operations.ImageOperation;
import com.ias.image.processing.logic.operations.OperationType;
import com.ias.image.processing.ui.MainFrame;

@SuppressWarnings("serial")
public class Sidebar2 extends JPanel {

    public MainFrame mainFrame;
    private JPanel mainPanel;

    private JButton addOperationsButton;

    public Sidebar2(MainFrame frame) {
        super(new BorderLayout());
        this.mainFrame = frame;

        this.setSize(new Dimension(400, 0));
        this.setPreferredSize(new Dimension(400, 0));
        this.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(true);
        header.add(new JLabel(" "), BorderLayout.CENTER);
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0), BorderFactory.createLineBorder(Color.GRAY)));

        JPanel leftHeaderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        leftHeaderPanel.setBackground(Color.WHITE);

        JButton undoButton = new JButton("Undo");
        undoButton.setBackground(Color.WHITE);
        undoButton.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        undoButton.setFocusable(false);
        undoButton.addActionListener(e -> {
            if (mainFrame.getImageController() != null) {
                mainFrame.getImageController().undo();
            }
        });
        leftHeaderPanel.add(undoButton);

        JButton enableAllBtn = new JButton("All On");
        enableAllBtn.setBackground(Color.WHITE);
        enableAllBtn.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        enableAllBtn.setFocusable(false);
        enableAllBtn.addActionListener(e -> {
            if (mainFrame.getImageController() != null) {
                ImageModel model = mainFrame.getImageController().getModel();
                for (ImageOperation op : model.getOperations()) {
                    op.setActive(true);
                }
                mainFrame.controller.processImage();
                rearrange();
            }
        });
        leftHeaderPanel.add(enableAllBtn);

        JButton disableAllBtn = new JButton("All Off");
        disableAllBtn.setBackground(Color.WHITE);
        disableAllBtn.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        disableAllBtn.setFocusable(false);
        disableAllBtn.addActionListener(e -> {
            if (mainFrame.getImageController() != null) {
                ImageModel model = mainFrame.getImageController().getModel();
                for (ImageOperation op : model.getOperations()) {
                    op.setActive(false);
                }
                mainFrame.controller.processImage();
                rearrange();
            }
        });
        leftHeaderPanel.add(disableAllBtn);
        header.add(leftHeaderPanel, BorderLayout.WEST);


        AddOperationsAction addOperations = new AddOperationsAction(mainFrame);
        addOperationsButton = new JButton(addOperations);
        addOperationsButton.setBackground(Color.WHITE);
        addOperationsButton.setBorder(null);
        addOperationsButton.setFocusable(false);
        addOperationsButton.setPreferredSize(new Dimension(24, 24));
        header.add(addOperationsButton, BorderLayout.EAST);
        this.add(header, BorderLayout.NORTH);

        mainPanel = new JPanel();
        mainPanel.setOpaque(true);
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
            case GAUSSIANBLUR:
                opUI = new GaussianUI(mainFrame, operation, index);
                break;
            case TILE:
                opUI = new TileUI(mainFrame, operation, index);
                break;
            case ROTATE:
                opUI = new RotateUI(mainFrame, operation, index);
                break;
            case COLOR_HISTOGRAM:
                opUI = new ColorHistogramUI(mainFrame, operation, index);
                break;
            case GRAYSCALE:
                opUI = new GrayscaleUI(mainFrame, operation, index);
                break;
            case THRESHOLD:
                opUI = new ThresholdUI(mainFrame, operation, index);
                break;
            case HISTOGRAM_EQ:
                opUI= new HistogramEqualizationUI(mainFrame, operation, index);
                break;
            case CONTRAST_STRETCH:
                opUI = new ContrastStretchingUI(mainFrame, operation, index);
                break;
            case KMEANS:
                opUI = new KMeansUI(mainFrame, operation, index);
                break;

            case BLOB_COUNTER:
                opUI = new BlobCounterUI(mainFrame, operation, index);
                break;

            case MORPHOLOGY:
                opUI = new MorphologyUI(mainFrame, operation, index);
                break;

            case EDGE_DETECTION:
                opUI = new EdgeDetectionUI(mainFrame, operation, index);
                break;

            default:
                System.out.println("Sidebar2.createOperationPanelUI(), there is not a valid ui for " + operation.getOperationType());
        }

        return opUI;
    }

    public void showOperationMenu() {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem blurItem = new JMenuItem(new AddOperationAction(mainFrame, OperationType.GAUSSIANBLUR));
        JMenuItem rotateItem = new JMenuItem(new AddOperationAction(mainFrame, OperationType.ROTATE));
        JMenuItem tileItem = new JMenuItem(new AddOperationAction(mainFrame, OperationType.TILE));
        JMenuItem histItem = new JMenuItem(new AddOperationAction(mainFrame, OperationType.COLOR_HISTOGRAM));
        JMenuItem grayItem  = new JMenuItem(new AddOperationAction(mainFrame, OperationType.GRAYSCALE));
        JMenuItem thresholdItem  = new JMenuItem(new AddOperationAction(mainFrame, OperationType.THRESHOLD));
        JMenuItem heItem = new JMenuItem(new AddOperationAction(mainFrame, OperationType.HISTOGRAM_EQ));
        JMenuItem csItem = new JMenuItem(new AddOperationAction(mainFrame, OperationType.CONTRAST_STRETCH));
        JMenuItem kmItem = new JMenuItem(new AddOperationAction(mainFrame, OperationType.KMEANS));
        JMenuItem blbItem = new JMenuItem(new AddOperationAction(mainFrame, OperationType.BLOB_COUNTER));
        JMenuItem mrpItem = new JMenuItem(new AddOperationAction(mainFrame, OperationType.MORPHOLOGY));
        JMenuItem edgItem = new JMenuItem(new AddOperationAction(mainFrame, OperationType.EDGE_DETECTION));



        JMenuItem cropItem = new JMenuItem("Crop");
        cropItem.addActionListener(e -> mainFrame.controller.setCropModeActive(true));


        menu.add(cropItem);
        menu.add(rotateItem);
        menu.add(tileItem);
        menu.addSeparator();
        menu.add(blurItem);
        menu.add(grayItem);
        menu.add(edgItem);
        menu.add(histItem);
        menu.addSeparator();
        menu.add(heItem);
        menu.add(csItem);
        menu.addSeparator();
        menu.add(kmItem);
        menu.add(thresholdItem);
        menu.add(mrpItem);
        menu.add(blbItem);


        menu.show(addOperationsButton, 0, addOperationsButton.getHeight());
    }

}