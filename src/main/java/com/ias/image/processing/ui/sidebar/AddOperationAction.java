package com.ias.image.processing.ui.sidebar;

import java.awt.event.ActionEvent;

import com.ias.image.processing.logic.operations.OperationType;
import com.ias.image.processing.ui.AbstractUIAction;
import com.ias.image.processing.ui.MainFrame;

@SuppressWarnings("serial")
public class AddOperationAction extends AbstractUIAction {

	OperationType type;

	public AddOperationAction(MainFrame ide, OperationType type) {
		super(ide, type.getDescription(), null);

		this.type = type;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		this.Frame.controller.addOperation(this.type);

	}

}
