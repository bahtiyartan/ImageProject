package com.ias.image.processing.ui.sidebar;

import java.awt.event.ActionEvent;

import com.ias.image.processing.ui.AbstractUIAction;
import com.ias.image.processing.ui.MainFrame;
import com.ias.image.processing.ui.resources.IDEIcons;

@SuppressWarnings("serial")
public class AddOperationsAction extends AbstractUIAction {

	public AddOperationsAction(MainFrame frame) {
		super(frame, "", IDEIcons.AddIcon, true, "Add New Operation");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Frame.sidebar2.showOperationMenu();
	}
}
