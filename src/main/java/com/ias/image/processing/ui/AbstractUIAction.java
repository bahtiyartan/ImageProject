package com.ias.image.processing.ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

@SuppressWarnings("serial")
public abstract class AbstractUIAction extends AbstractAction {

	protected MainFrame Frame;
	private String tooltip;

	public AbstractUIAction(MainFrame ide, String name, Icon icon) {
		this(ide, name, icon, true, name);
	}

	public AbstractUIAction(MainFrame ide, String name, Icon icon, boolean enabled) {
		this(ide, name, icon, enabled, name);
	}

	public AbstractUIAction(MainFrame ide, String name, Icon icon, boolean enabled, String tooltip) {
		super(name, icon);
		this.Frame = ide;
		this.setEnabled(enabled);
		this.tooltip = tooltip;
	}

	public Icon getIcon() {
		return (Icon) getValue(Action.SMALL_ICON);
	}

	public String getName() {
		return getValue(Action.NAME).toString();
	}

	public String getTooltip() {
		return tooltip;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("AbstractUIAction.actionPerformed()");
	}

}
