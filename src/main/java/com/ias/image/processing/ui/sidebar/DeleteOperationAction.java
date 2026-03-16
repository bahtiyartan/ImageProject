package com.ias.image.processing.ui.sidebar;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import com.ias.image.processing.logic.operations.ImageOperation;
import com.ias.image.processing.ui.AbstractUIAction;
import com.ias.image.processing.ui.MainFrame;
import com.ias.image.processing.ui.resources.IDEIcons;

@SuppressWarnings("serial")
public class DeleteOperationAction extends AbstractUIAction {

	public ImageOperation operation;
	public int index; // TODO:remove

	public DeleteOperationAction(MainFrame frame, ImageOperation operation, int index) {
		super(frame, "", IDEIcons.DeleteIcon, true, "Delete Operation");

		this.operation = operation;
		this.index = index;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		int confirm = JOptionPane.showConfirmDialog(this.Frame, "Are you sure you want to delete this step?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

		if (confirm == JOptionPane.YES_OPTION) {
			// controller.removeOperation(index);
			this.Frame.controller.removeOperation(index);
		}

	}

}
